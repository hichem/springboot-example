package com.example.springbootexample.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = { "com.example.springbootexample" })
public class InsuranceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceApplication.class, args);
	}

}
