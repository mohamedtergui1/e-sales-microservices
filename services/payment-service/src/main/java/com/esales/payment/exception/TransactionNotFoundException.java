package com.esales.payment.exception;

public class TransactionNotFoundException extends PaymentException {


    public TransactionNotFoundException(Long id) {
        super("Transaction non trouvée avec l'ID: " + id);
    }

    public TransactionNotFoundException(String reference) {
        super("Transaction non trouvée avec la référence: " + reference);
    }
}
