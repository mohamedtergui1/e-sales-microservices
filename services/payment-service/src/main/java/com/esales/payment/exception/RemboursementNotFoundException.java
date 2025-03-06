package com.esales.payment.exception;

public class RemboursementNotFoundException extends PaymentException {

    public RemboursementNotFoundException(String message) {
        super(message);
    }

    public RemboursementNotFoundException(Long id) {
        super("Remboursement non trouvé avec l'ID: " + id);
    }
}