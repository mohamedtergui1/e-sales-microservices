package com.esales.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class PaymentService000Application {

    public static void main(String[] args) {
        SpringApplication.run(PaymentService000Application.class, args);
    }

}
