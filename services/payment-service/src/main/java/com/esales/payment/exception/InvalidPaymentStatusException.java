package com.esales.payment.exception;

public class InvalidPaymentStatusException extends PaymentException {

    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
