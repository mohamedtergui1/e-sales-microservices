package com.esales.payment.dto;

import com.esales.payment.model.Paiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDTO {
    private Long id;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "L'ID de commande est obligatoire")
    private Long commandeId;

    @NotNull(message = "Le moyen de paiement est obligatoire")
    private Paiement.MoyenPaiement moyenPaiement;

    private LocalDateTime dateCreation;
    private Paiement.StatutPaiement statut;
}
