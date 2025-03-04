package com.esales.payment.service;

import com.esales.payment.dto.RemboursementDTO;
import com.esales.payment.exception.InvalidPaymentStatusException;
import com.esales.payment.exception.PaymentNotFoundException;
import com.esales.payment.exception.RemboursementNotFoundException;
import com.esales.payment.model.Paiement;
import com.esales.payment.model.Remboursement;
import com.esales.payment.model.Transaction;
import com.esales.payment.repository.PaiementRepository;
import com.esales.payment.repository.RemboursementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final PaiementRepository paiementRepository;
    private final TransactionService transactionService;
    @Autowired
    private OrderServiceClient orderServiceClient;

    @Transactional
    public RemboursementDTO demanderRemboursement(RemboursementDTO remboursementDTO) {
        Long paiementId = remboursementDTO.getPaiementId();
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        // Vérifier que le paiement est validé
        if (paiement.getStatut() != Paiement.StatutPaiement.VALIDE &&
                paiement.getStatut() != Paiement.StatutPaiement.PARTIELLEMENT_REMBOURSE) {
            throw new InvalidPaymentStatusException("Le remboursement n'est possible que pour un paiement validé");
        }

        // Vérifier que le montant du remboursement ne dépasse pas le montant du paiement
        BigDecimal montantDejaRembourse = paiement.getRemboursements().stream()
                .filter(r -> r.getStatut() == Remboursement.StatutRemboursement.VALIDE)
                .map(Remboursement::getMontantRembourse)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantRemboursable = paiement.getMontant().subtract(montantDejaRembourse);

        if (remboursementDTO.getMontantRembourse().compareTo(montantRemboursable) > 0) {
            throw new InvalidPaymentStatusException("Le montant du remboursement dépasse le montant remboursable");
        }

        // Créer le remboursement
        Remboursement remboursement = new Remboursement();
        remboursement.setPaiement(paiement);
        remboursement.setMontantRembourse(remboursementDTO.getMontantRembourse());
        remboursement.setRaisonRemboursement(remboursementDTO.getRaisonRemboursement());
        remboursement.setStatut(Remboursement.StatutRemboursement.DEMANDE);
        remboursement.setReferenceRemboursement("REFUND-" + UUID.randomUUID().toString());

        remboursement = remboursementRepository.save(remboursement);

        // Notification au service de commande
        orderServiceClient.notifyRefundRequested(paiement.getCommandeId(), remboursement.getId());

        return mapToDTO(remboursement);
    }

    @Transactional
    public RemboursementDTO validerRemboursement(Long remboursementId) {
        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new RemboursementNotFoundException(remboursementId));

        if (remboursement.getStatut() != Remboursement.StatutRemboursement.DEMANDE &&
                remboursement.getStatut() != Remboursement.StatutRemboursement.EN_COURS) {
            throw new InvalidPaymentStatusException("Le remboursement ne peut être validé que s'il est en demande ou en cours");
        }

        remboursement.setStatut(Remboursement.StatutRemboursement.VALIDE);

        // Mettre à jour le statut du paiement
        Paiement paiement = remboursement.getPaiement();

        BigDecimal montantTotal = paiement.getMontant();
        BigDecimal montantRembourse = paiement.getRemboursements().stream()
                .filter(r -> r.getStatut() == Remboursement.StatutRemboursement.VALIDE ||
                        r.getId().equals(remboursementId))
                .map(Remboursement::getMontantRembourse)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Si montant remboursé = montant total, alors le paiement est entièrement remboursé
        if (montantRembourse.compareTo(montantTotal) >= 0) {
            paiement.setStatut(Paiement.StatutPaiement.REMBOURSE);
        } else {
            paiement.setStatut(Paiement.StatutPaiement.PARTIELLEMENT_REMBOURSE);
        }

        paiementRepository.save(paiement);

        // Créer une transaction pour le remboursement
        Transaction transaction = new Transaction();
        transaction.setPaiement(paiement);
        transaction.setReference("REFUND-TX-" + UUID.randomUUID().toString());
        transaction.setStatut(Transaction.StatutTransaction.REUSSIE);
        transaction.setDetailsTransaction("Remboursement validé: " + remboursement.getReferenceRemboursement());

        transactionService.saveTransaction(transaction);

        // Notification au service de commande
        orderServiceClient.notifyRefundValidated(paiement.getCommandeId(), remboursement.getId());

        return mapToDTO(remboursementRepository.save(remboursement));
    }

    @Transactional
    public RemboursementDTO refuserRemboursement(Long remboursementId, String raison) {
        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new RemboursementNotFoundException(remboursementId));

        if (remboursement.getStatut() != Remboursement.StatutRemboursement.DEMANDE &&
                remboursement.getStatut() != Remboursement.StatutRemboursement.EN_COURS) {
            throw new InvalidPaymentStatusException("Le remboursement ne peut être refusé que s'il est en demande ou en cours");
        }

        remboursement.setStatut(Remboursement.StatutRemboursement.REFUSE);
        remboursement.setRaisonRemboursement(remboursement.getRaisonRemboursement() + " | REFUSÉ: " + raison);

        // Notification au service de commande
        orderServiceClient.notifyRefundRejected(remboursement.getPaiement().getCommandeId(), remboursementId, raison);

        return mapToDTO(remboursementRepository.save(remboursement));
    }

    @Transactional(readOnly = true)
    public RemboursementDTO getRemboursementById(Long id) {
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new RemboursementNotFoundException(id));
        return mapToDTO(remboursement);
    }

    @Transactional(readOnly = true)
    public List<RemboursementDTO> getRemboursementsByPaiementId(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        return remboursementRepository.findByPaiement(paiement).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RemboursementDTO mapToDTO(Remboursement remboursement) {
        RemboursementDTO dto = new RemboursementDTO();
        dto.setId(remboursement.getId());
        dto.setPaiementId(remboursement.getPaiement().getId());
        dto.setMontantRembourse(remboursement.getMontantRembourse());
        dto.setDateRemboursement(remboursement.getDateRemboursement());
        dto.setStatut(remboursement.getStatut());
        dto.setRaisonRemboursement(remboursement.getRaisonRemboursement());
        dto.setReferenceRemboursement(remboursement.getReferenceRemboursement());
        return dto;
    }
}