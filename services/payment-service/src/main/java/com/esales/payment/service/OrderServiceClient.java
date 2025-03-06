package com.esales.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "${order.service.url}")
public interface OrderServiceClient {

    @PostMapping("/api/orders/{orderId}/payment-validated")
    void notifyPaymentValidated(@PathVariable("orderId") Long orderId);

    @PostMapping("/api/orders/{orderId}/payment-rejected")
    void notifyPaymentRejected(@PathVariable("orderId") Long orderId, @RequestParam("reason") String reason);

    @PostMapping("/api/orders/{orderId}/payment-expired")
    void notifyPaymentExpired(@PathVariable("orderId") Long orderId);

    @PostMapping("/api/orders/{orderId}/refund-requested")
    void notifyRefundRequested(@PathVariable("orderId") Long orderId, @RequestParam("refundId") Long refundId);

    @PostMapping("/api/orders/{orderId}/refund-validated")
    void notifyRefundValidated(@PathVariable("orderId") Long orderId, @RequestParam("refundId") Long refundId);

    @PostMapping("/api/orders/{orderId}/refund-rejected")
    void notifyRefundRejected(
            @PathVariable("orderId") Long orderId,
            @RequestParam("refundId") Long refundId,
            @RequestParam("reason") String reason);
}