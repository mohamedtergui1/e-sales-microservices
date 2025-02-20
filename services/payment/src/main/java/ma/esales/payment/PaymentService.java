package ma.esales.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public UUID createPayment(PaymentRequest paymentRequest) {
        Payment createdPayment = paymentRepository.save(paymentMapper.toEntity(paymentRequest));
        return createdPayment.getId();
    }

}
