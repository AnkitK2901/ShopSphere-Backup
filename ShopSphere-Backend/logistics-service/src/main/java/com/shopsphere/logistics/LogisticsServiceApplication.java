package com.shopsphere.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // ADDED
import org.springframework.scheduling.annotation.EnableScheduling; // UNCOMMENTED

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // THE FIX: Enables the Feign Client to talk to Order Service
@EnableScheduling   // THE FIX: Enables background shipment simulation
public class LogisticsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogisticsServiceApplication.class, args);
	}
}