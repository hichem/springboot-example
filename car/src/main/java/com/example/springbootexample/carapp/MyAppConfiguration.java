package com.example.springbootexample.carapp;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbootexample.carapp.service.MessageSender;
import com.example.springbootexample.carapp.service.MessageSenderImpl;


@Configuration
public class MyAppConfiguration {

	@Bean
	public Queue carQueue() {
		return new Queue(AmqpConfig.TOPIC_CAR);
	}
	
	@Bean
	public MessageSender messageSender() {
		return new MessageSenderImpl();
	}
	
}
