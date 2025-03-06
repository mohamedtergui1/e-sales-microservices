package com.esales.payment.repository;

import com.esales.payment.model.Paiement;
import com.esales.payment.model.Remboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    List<Remboursement> findByPaiement(Paiement paiement);
    Optional<Remboursement> findByReferenceRemboursement(String reference);
    List<Remboursement> findByStatut(Remboursement.StatutRemboursement statut);
}