package com.esales.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut;

    @Column(nullable = false)
    private LocalDateTime dateTransaction;

    @Column(length = 1000)
    private String detailsTransaction;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "stripe_payment_method")
    private String stripePaymentMethod;

    public enum StatutTransaction {
        INITIEE,
        EN_COURS,
        REUSSIE,
        ECHOUEE,
        ANNULEE
    }

    @PrePersist
    protected void onCreate() {
        dateTransaction = LocalDateTime.now();
    }
}