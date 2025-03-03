package com.esales.payment.dto;

import com.esales.payment.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long paiementId;
    private String reference;
    private Transaction.StatutTransaction statut;
    private LocalDateTime dateTransaction;
    private String detailsTransaction;
}