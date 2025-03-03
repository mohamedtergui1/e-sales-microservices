package com.esales.payment.config;

import com.stripe.Stripe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostConstruct
    public void setup() {
        log.info("Initialisation de l'API Stripe");
        Stripe.apiKey = stripeApiKey;
    }

    @Bean
    public String stripeWebhookSecret() {
        return webhookSecret;
    }
}