package com.esales.payment.service;

import com.esales.payment.dto.TransactionDTO;
import com.esales.payment.exception.PaymentNotFoundException;
import com.esales.payment.exception.TransactionNotFoundException;
import com.esales.payment.model.Paiement;
import com.esales.payment.model.Transaction;
import com.esales.payment.repository.PaiementRepository;
import com.esales.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaiementRepository paiementRepository;

    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return mapToDTO(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new TransactionNotFoundException(reference));
        return mapToDTO(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByPaiementId(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaymentNotFoundException(paiementId));

        return transactionRepository.findByPaiement(paiement).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionDTO updateTransactionStatus(Long id, Transaction.StatutTransaction statut, String details) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transaction.setStatut(statut);

        if (details != null && !details.isEmpty()) {
            transaction.setDetailsTransaction(details);
        }

        return mapToDTO(transactionRepository.save(transaction));
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setPaiementId(transaction.getPaiement().getId());
        dto.setReference(transaction.getReference());
        dto.setStatut(transaction.getStatut());
        dto.setDateTransaction(transaction.getDateTransaction());
        dto.setDetailsTransaction(transaction.getDetailsTransaction());
        return dto;
    }
}