package com.esales.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "remboursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @Column(nullable = false)
    private BigDecimal montantRembourse;

    @Column(nullable = false)
    private LocalDateTime dateRemboursement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRemboursement statut;

    @Column(length = 1000)
    private String raisonRemboursement;

    @Column(unique = true)
    private String referenceRemboursement;

    public enum StatutRemboursement {
        DEMANDE,
        EN_COURS,
        VALIDE,
        REFUSE
    }

    @PrePersist
    protected void onCreate() {
        dateRemboursement = LocalDateTime.now();
    }
}