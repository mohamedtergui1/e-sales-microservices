package ma.esales.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public Payment toEntity(final PaymentRequest paymentRequest) {
        return Payment.builder().amount(paymentRequest.amount()).paymentMethod(paymentRequest.paymentMethod()).orderId(paymentRequest.orderId()).build();
    }
}
