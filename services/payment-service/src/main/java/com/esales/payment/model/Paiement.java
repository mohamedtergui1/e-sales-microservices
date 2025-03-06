package com.esales.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(nullable = false)
    private Long commandeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoyenPaiement moyenPaiement;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Remboursement> remboursements = new ArrayList<>();

    public enum MoyenPaiement {
        CARTE_BANCAIRE,
        PAYPAL,
        VIREMENT
    }

    public enum StatutPaiement {
        EN_ATTENTE,
        VALIDE,
        REFUSE,
        EXPIRE,
        REMBOURSE,
        PARTIELLEMENT_REMBOURSE
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}