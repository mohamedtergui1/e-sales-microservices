package com.esales.payment.repository;

import com.esales.payment.model.Paiement;
import com.esales.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPaiement(Paiement paiement);
    Optional<Transaction> findByReference(String reference);
    List<Transaction> findByStatut(Transaction.StatutTransaction statut);
}