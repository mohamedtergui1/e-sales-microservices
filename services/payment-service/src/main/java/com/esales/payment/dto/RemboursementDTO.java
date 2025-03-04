package com.esales.payment.dto;


import com.esales.payment.model.Remboursement;
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
public class RemboursementDTO {
    private Long id;

    @NotNull(message = "L'ID de paiement est obligatoire")
    private Long paiementId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montantRembourse;

    private LocalDateTime dateRemboursement;
    private Remboursement.StatutRemboursement statut;
    private String raisonRemboursement;
    private String referenceRemboursement;
}