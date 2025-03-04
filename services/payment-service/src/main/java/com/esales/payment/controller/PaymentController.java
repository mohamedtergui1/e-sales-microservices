package com.esales.payment.controller;

import com.esales.payment.dto.PaiementDTO;
import com.esales.payment.dto.RemboursementDTO;
import com.esales.payment.dto.TransactionDTO;
import com.esales.payment.service.PaymentService;
import com.esales.payment.service.RemboursementService;
import com.esales.payment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final RemboursementService remboursementService;

    @PostMapping
    public ResponseEntity<PaiementDTO> creerPaiement(@Valid @RequestBody PaiementDTO paiementDTO) {
        log.info("Création d'un nouveau paiement pour la commande: {}", paiementDTO.getCommandeId());
        PaiementDTO resultat = paymentService.creerPaiement(paiementDTO);
        return new ResponseEntity<>(resultat, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable Long id) {
        log.info("Récupération du paiement avec ID: {}", id);
        return ResponseEntity.ok(paymentService.getPaiementById(id));
    }

    @GetMapping("/by-order/{commandeId}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByCommandeId(@PathVariable Long commandeId) {
        log.info("Récupération des paiements pour la commande: {}", commandeId);
        return ResponseEntity.ok(paymentService.getPaiementsByCommandeId(commandeId));
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<PaiementDTO> validerPaiement(@PathVariable Long id) {
        log.info("Validation du paiement avec ID: {}", id);
        return ResponseEntity.ok(paymentService.validerPaiement(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PaiementDTO> refuserPaiement(
            @PathVariable Long id,
            @RequestParam String raison) {
        log.info("Refus du paiement avec ID: {} pour la raison: {}", id, raison);
        return ResponseEntity.ok(paymentService.refuserPaiement(id, raison));
    }

    // Endpoints pour les transactions
    @GetMapping("/{paiementId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByPaiementId(@PathVariable Long paiementId) {
        log.info("Récupération des transactions pour le paiement: {}", paiementId);
        return ResponseEntity.ok(transactionService.getTransactionsByPaiementId(paiementId));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        log.info("Récupération de la transaction avec ID: {}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/transactions/by-reference/{reference}")
    public ResponseEntity<TransactionDTO> getTransactionByReference(@PathVariable String reference) {
        log.info("Récupération de la transaction avec référence: {}", reference);
        return ResponseEntity.ok(transactionService.getTransactionByReference(reference));
    }

    // Endpoints pour les remboursements
    @PostMapping("/refunds")
    public ResponseEntity<RemboursementDTO> demanderRemboursement(@Valid @RequestBody RemboursementDTO remboursementDTO) {
        log.info("Demande de remboursement pour le paiement: {}", remboursementDTO.getPaiementId());
        RemboursementDTO resultat = remboursementService.demanderRemboursement(remboursementDTO);
        return new ResponseEntity<>(resultat, HttpStatus.CREATED);
    }

    @GetMapping("/refunds/{id}")
    public ResponseEntity<RemboursementDTO> getRemboursementById(@PathVariable Long id) {
        log.info("Récupération du remboursement avec ID: {}", id);
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }

    @GetMapping("/{paiementId}/refunds")
    public ResponseEntity<List<RemboursementDTO>> getRemboursementsByPaiementId(@PathVariable Long paiementId) {
        log.info("Récupération des remboursements pour le paiement: {}", paiementId);
        return ResponseEntity.ok(remboursementService.getRemboursementsByPaiementId(paiementId));
    }

    @PostMapping("/refunds/{id}/validate")
    public ResponseEntity<RemboursementDTO> validerRemboursement(@PathVariable Long id) {
        log.info("Validation du remboursement avec ID: {}", id);
        return ResponseEntity.ok(remboursementService.validerRemboursement(id));
    }

    @PostMapping("/refunds/{id}/reject")
    public ResponseEntity<RemboursementDTO> refuserRemboursement(
            @PathVariable Long id,
            @RequestParam String raison) {
        log.info("Refus du remboursement avec ID: {} pour la raison: {}", id, raison);
        return ResponseEntity.ok(remboursementService.refuserRemboursement(id, raison));
    }
}