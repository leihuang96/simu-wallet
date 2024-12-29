package com.simuwallet.exchange_rate_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.simuwallet.exchange_rate_service.client")
public class ExchangeRateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateServiceApplication.class, args);
	}

}
