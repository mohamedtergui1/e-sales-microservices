package com.esales.payment.repository;

import com.esales.payment.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByCommandeId(Long commandeId);
    List<Paiement> findByStatut(Paiement.StatutPaiement statut);
    List<Paiement> findByDateCreationBeforeAndStatut(LocalDateTime date, Paiement.StatutPaiement statut);
}
