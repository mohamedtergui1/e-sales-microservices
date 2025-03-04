package com.esales.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component()
@Primary
@Profile("dev")
@Slf4j
public class OrderServiceClientMock implements OrderServiceClient {

    @Override
    public void notifyPaymentValidated(Long orderId) {
        log.info("MOCK: Notification au service de commande - Paiement validé pour la commande {}", orderId);
    }

    @Override
    public void notifyPaymentRejected(Long orderId, String reason) {
        log.info("MOCK: Notification au service de commande - Paiement refusé pour la commande {} : {}", orderId, reason);
    }

    @Override
    public void notifyPaymentExpired(Long orderId) {
        log.info("MOCK: Notification au service de commande - Paiement expiré pour la commande {}", orderId);
    }

    @Override
    public void notifyRefundRequested(Long orderId, Long refundId) {
        log.info("MOCK: Notification au service de commande - Remboursement demandé pour la commande {}, remboursement {}", orderId, refundId);
    }

    @Override
    public void notifyRefundValidated(Long orderId, Long refundId) {
        log.info("MOCK: Notification au service de commande - Remboursement validé pour la commande {}, remboursement {}", orderId, refundId);
    }

    @Override
    public void notifyRefundRejected(Long orderId, Long refundId, String reason) {
        log.info("MOCK: Notification au service de commande - Remboursement refusé pour la commande {}, remboursement {}: {}", orderId, refundId, reason);
    }
}