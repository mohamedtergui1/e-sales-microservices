package com.esales.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // Register with Eureka, not be Eureka
public class OrdersServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrdersServiceApplication.class, args);
	}
}