package com.esales.payment.service;

import com.esales.payment.dto.StripePaymentDTO;
import com.esales.payment.exception.PaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.publishable.key:pk_test_votrepublishablekey}")
    private String stripePublishableKey;

    /**
     * Crée une intention de paiement avec Stripe
     * @param montant Montant à débiter en euros
     * @param description Description du paiement
     * @param metadata Métadonnées additionnelles
     * @return DTO contenant les informations nécessaires pour finaliser le paiement côté client
     */
    public StripePaymentDTO creerIntentionPaiement(BigDecimal montant, String description, Map<String, String> metadata) {
        try {
            // Conversion du montant en centimes (Stripe utilise les centimes)
            long montantCentimes = montant.multiply(new BigDecimal(100)).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(montantCentimes)
                    .setCurrency("eur")
                    .setDescription(description)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );


            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            log.info("Intention de paiement Stripe créée avec succès: {}", paymentIntent.getId());

            return new StripePaymentDTO(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    stripePublishableKey
            );
        } catch (StripeException e) {
            log.error("Erreur lors de la création de l'intention de paiement Stripe", e);
            throw new PaymentException("Erreur lors de la création de l'intention de paiement: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère une intention de paiement par son ID
     * @param paymentIntentId ID de l'intention de paiement
     * @return L'intention de paiement
     */
    public PaymentIntent recupererIntentionPaiement(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            log.info("Intention de paiement récupérée: {}, statut: {}", paymentIntentId, paymentIntent.getStatus());
            return paymentIntent;
        } catch (StripeException e) {
            log.error("Erreur lors de la récupération de l'intention de paiement: {}", paymentIntentId, e);
            throw new PaymentException("Erreur lors de la récupération de l'intention de paiement: " + e.getMessage(), e);
        }
    }

    /**
     * Effectue un remboursement pour un paiement Stripe
     * @param paymentIntentId ID de l'intention de paiement
     * @param montant Montant à rembourser (null pour un remboursement total)
     * @param raison Raison du remboursement
     * @return Le remboursement créé
     */
    public Refund effectuerRemboursement(String paymentIntentId, BigDecimal montant, String raison) {
        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);

            // Si montant spécifié, on fait un remboursement partiel
            if (montant != null) {
                long montantCentimes = montant.multiply(new BigDecimal(100)).longValue();
                paramsBuilder.setAmount(montantCentimes);
            }

            Map<String, String> metadata = new HashMap<>();
            metadata.put("raison", raison);
            paramsBuilder.setMetadata(metadata);

            Refund refund = Refund.create(paramsBuilder.build());
            log.info("Remboursement Stripe créé avec succès: {}", refund.getId());

            return refund;
        } catch (StripeException e) {
            log.error("Erreur lors du remboursement Stripe", e);
            throw new PaymentException("Erreur lors du remboursement: " + e.getMessage(), e);
        }
    }
}