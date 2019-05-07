package com.example.springbootexample.carapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = { "com.example.springbootexample" })
public class CarApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarApplication.class, args);
	}

}
