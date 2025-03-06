package com.esales.payment.service;

import com.esales.payment.dto.PaiementDTO;
import com.esales.payment.exception.InvalidPaymentStatusException;
import com.esales.payment.exception.PaymentNotFoundException;
import com.esales.payment.model.Paiement;
import com.esales.payment.model.Transaction;
import com.esales.payment.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaiementRepository paiementRepository;
    private final TransactionService transactionService;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Value("${payment.expiration.delay:24}")
    private int paymentExpirationDelay;

    @Transactional
    public PaiementDTO creerPaiement(PaiementDTO paiementDTO) {
        Paiement paiement = new Paiement();
        paiement.setMontant(paiementDTO.getMontant());
        paiement.setCommandeId(paiementDTO.getCommandeId());
        paiement.setMoyenPaiement(paiementDTO.getMoyenPaiement());
        paiement.setStatut(Paiement.StatutPaiement.EN_ATTENTE);

        paiement = paiementRepository.save(paiement);

        // Création d'une transaction initiale
        Transaction transaction = new Transaction();
        transaction.setPaiement(paiement);
        transaction.setReference(generateTransactionReference());
        transaction.setStatut(Transaction.StatutTransaction.INITIEE);
        transaction.setDetailsTransaction("Initialisation du paiement");

        transactionService.saveTransaction(transaction);

        return mapToDTO(paiement);
    }

    @Transactional
    public void updateStripePaymentIntentId(Long paiementId, String paymentIntentId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        // On recherche la transaction la plus récente
        if (!paiement.getTransactions().isEmpty()) {
            Transaction lastTransaction = paiement.getTransactions().get(paiement.getTransactions().size() - 1);
            lastTransaction.setStripePaymentIntentId(paymentIntentId);
            transactionService.saveTransaction(lastTransaction);
            log.info("PaymentIntent ID Stripe mis à jour pour la transaction: {}", lastTransaction.getId());
        }
    }

    @Transactional
    public void updateStripePaymentMethod(Long paiementId, String paymentMethod) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        // On recherche la transaction la plus récente
        if (!paiement.getTransactions().isEmpty()) {
            Transaction lastTransaction = paiement.getTransactions().get(paiement.getTransactions().size() - 1);
            lastTransaction.setStripePaymentMethod(paymentMethod);
            transactionService.saveTransaction(lastTransaction);
            log.info("Payment Method ID Stripe mis à jour pour la transaction: {}", lastTransaction.getId());
        }
    }

    @Transactional(readOnly = true)
    public PaiementDTO getPaiementById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return mapToDTO(paiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementDTO> getPaiementsByCommandeId(Long commandeId) {
        return paiementRepository.findByCommandeId(commandeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaiementDTO validerPaiement(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        if (paiement.getStatut() != Paiement.StatutPaiement.EN_ATTENTE) {
            throw new InvalidPaymentStatusException("Le paiement ne peut être validé que s'il est en attente");
        }

        paiement.setStatut(Paiement.StatutPaiement.VALIDE);

        // Création d'une transaction pour la validation
        Transaction transaction = new Transaction();
        transaction.setPaiement(paiement);
        transaction.setReference(generateTransactionReference());
        transaction.setStatut(Transaction.StatutTransaction.REUSSIE);
        transaction.setDetailsTransaction("Paiement validé");

        transactionService.saveTransaction(transaction);

        // Notification au service de commande
        orderServiceClient.notifyPaymentValidated(paiement.getCommandeId());

        return mapToDTO(paiementRepository.save(paiement));
    }

    @Transactional
    public PaiementDTO refuserPaiement(Long paiementId, String raison) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        if (paiement.getStatut() != Paiement.StatutPaiement.EN_ATTENTE) {
            throw new InvalidPaymentStatusException("Le paiement ne peut être refusé que s'il est en attente");
        }

        paiement.setStatut(Paiement.StatutPaiement.REFUSE);

        // Création d'une transaction pour le refus
        Transaction transaction = new Transaction();
        transaction.setPaiement(paiement);
        transaction.setReference(generateTransactionReference());
        transaction.setStatut(Transaction.StatutTransaction.ECHOUEE);
        transaction.setDetailsTransaction("Paiement refusé: " + raison);

        transactionService.saveTransaction(transaction);

        // Notification au service de commande
        orderServiceClient.notifyPaymentRejected(paiement.getCommandeId(), raison);

        return mapToDTO(paiementRepository.save(paiement));
    }

    @Scheduled(fixedRate = 3600000) // Vérification toutes les heures
    @Transactional
    public void verifierPaiementsExpires() {
        LocalDateTime limitDate = LocalDateTime.now().minusHours(paymentExpirationDelay);
        List<Paiement> paiementsExpires = paiementRepository
                .findByDateCreationBeforeAndStatut(limitDate, Paiement.StatutPaiement.EN_ATTENTE);

        for (Paiement paiement : paiementsExpires) {
            paiement.setStatut(Paiement.StatutPaiement.EXPIRE);

            // Création d'une transaction pour l'expiration
            Transaction transaction = new Transaction();
            transaction.setPaiement(paiement);
            transaction.setReference(generateTransactionReference());
            transaction.setStatut(Transaction.StatutTransaction.ANNULEE);
            transaction.setDetailsTransaction("Paiement expiré après " + paymentExpirationDelay + " heures");

            transactionService.saveTransaction(transaction);

            // Notification au service de commande
            orderServiceClient.notifyPaymentExpired(paiement.getCommandeId());

            paiementRepository.save(paiement);
            log.info("Paiement {} expiré pour la commande {}", paiement.getId(), paiement.getCommandeId());
        }
    }

    private String generateTransactionReference() {
        return "TX-" + UUID.randomUUID().toString();
    }

    private PaiementDTO mapToDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setMontant(paiement.getMontant());
        dto.setCommandeId(paiement.getCommandeId());
        dto.setMoyenPaiement(paiement.getMoyenPaiement());
        dto.setDateCreation(paiement.getDateCreation());
        dto.setStatut(paiement.getStatut());
        return dto;
    }
}