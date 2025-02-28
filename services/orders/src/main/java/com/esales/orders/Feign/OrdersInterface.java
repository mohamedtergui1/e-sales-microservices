package com.esales.orders.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@FeignClient("PRODUCT-SERVICE")
public interface OrdersInterface {
    @PostMapping("/{id}/{quantity}")
    BigDecimal processOrder(@PathVariable Long id, @PathVariable BigDecimal quantity);
}
