package com.esales.payment.controller;

import com.esales.payment.dto.PaiementDTO;
import com.esales.payment.dto.StripePaymentDTO;
import com.esales.payment.model.Paiement;
import com.esales.payment.service.PaymentService;
import com.esales.payment.service.StripeService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeController {

    private final StripeService stripeService;
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /**
     * Crée une intention de paiement avec Stripe et un paiement en attente dans notre système
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<StripePaymentDTO> creerIntentionPaiement(@Valid @RequestBody PaiementDTO paiementDTO) {
        log.info("Création d'une intention de paiement Stripe pour la commande: {}", paiementDTO.getCommandeId());

        // Vérifier que le moyen de paiement est bien CARTE_BANCAIRE
        if (paiementDTO.getMoyenPaiement() != Paiement.MoyenPaiement.CARTE_BANCAIRE) {
            paiementDTO.setMoyenPaiement(Paiement.MoyenPaiement.CARTE_BANCAIRE);
        }

        // Créer d'abord un paiement dans notre système
        PaiementDTO paiementCree = paymentService.creerPaiement(paiementDTO);

        // Préparer les métadonnées pour Stripe
        Map<String, String> metadata = new HashMap<>();
        metadata.put("commandeId", paiementDTO.getCommandeId().toString());
        metadata.put("paiementId", paiementCree.getId().toString());

        // Description pour Stripe
        String description = "Paiement pour la commande #" + paiementDTO.getCommandeId();

        // Créer l'intention de paiement Stripe
        StripePaymentDTO stripePaymentDTO = stripeService.creerIntentionPaiement(
                paiementDTO.getMontant(),
                description,
                metadata
        );

        // Enregistrer l'ID de l'intention de paiement Stripe dans notre transaction
        paymentService.updateStripePaymentIntentId(paiementCree.getId(), stripePaymentDTO.getPaymentIntentId());

        return new ResponseEntity<>(stripePaymentDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint pour les webhooks Stripe
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Réception d'un webhook Stripe");

        try {
            // Vérifier la signature du webhook
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                log.warn("Impossible de désérialiser l'objet Stripe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error");
            }

            // Gérer les événements en fonction de leur type
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    handlePaymentIntentSucceeded(paymentIntent);
                    break;

                case "payment_intent.payment_failed":
                    paymentIntent = (PaymentIntent) stripeObject;
                    handlePaymentIntentFailed(paymentIntent);
                    break;

                default:
                    log.info("Type d'événement non géré: {}", event.getType());
                    break;
            }

            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            log.error("Erreur lors du traitement du webhook Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }
    }

    /**
     * Méthode pour gérer un paiement réussi
     */
    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        log.info("Paiement Stripe réussi: {}", paymentIntent.getId());

        // Récupérer l'ID du paiement depuis les métadonnées
        Map<String, String> metadata = paymentIntent.getMetadata();
        if (metadata != null && metadata.containsKey("paiementId")) {
            Long paiementId = Long.parseLong(metadata.get("paiementId"));
            paymentService.validerPaiement(paiementId);

            // Mettre à jour les détails de la méthode de paiement
            if (paymentIntent.getPaymentMethod() != null) {
                paymentService.updateStripePaymentMethod(paiementId, paymentIntent.getPaymentMethod());
            }
        } else {
            log.warn("Paiement Stripe sans metadata valide: {}", paymentIntent.getId());
        }
    }

    /**
     * Méthode pour gérer un paiement échoué
     */
    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        log.info("Paiement Stripe échoué: {}", paymentIntent.getId());

        // Récupérer l'ID du paiement depuis les métadonnées
        Map<String, String> metadata = paymentIntent.getMetadata();
        if (metadata != null && metadata.containsKey("paiementId")) {
            Long paiementId = Long.parseLong(metadata.get("paiementId"));

            String raison = "Paiement refusé par le prestataire";
            if (paymentIntent.getLastPaymentError() != null && paymentIntent.getLastPaymentError().getMessage() != null) {
                raison = paymentIntent.getLastPaymentError().getMessage();
            }

            paymentService.refuserPaiement(paiementId, raison);
        } else {
            log.warn("Paiement Stripe échoué sans metadata valide: {}", paymentIntent.getId());
        }
    }

    /**
     * Vérifier le statut d'un paiement Stripe
     */
    @GetMapping("/check-status/{paymentIntentId}")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable String paymentIntentId) {
        PaymentIntent paymentIntent = stripeService.recupererIntentionPaiement(paymentIntentId);
        return ResponseEntity.ok(paymentIntent.getStatus());
    }
}