package com.esales.payment.exception;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(Long id) {
        super("Paiement non trouvé avec l'ID: " + id);
    }
}