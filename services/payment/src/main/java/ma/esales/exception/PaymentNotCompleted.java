package ma.esales.exception;

public class PaymentNotCompleted extends RuntimeException {
    public PaymentNotCompleted(String message) {
        super(message);
    }
}
