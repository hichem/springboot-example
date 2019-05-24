package com.example.springbootexample.insurance;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbootexample.insurance.model.MessageReceiver;



@Configuration
public class MyAppConfiguration {

	@Bean
	public MessageReceiver getMessageReceiver() {
		return new MessageReceiver();
	}
	
	
	//Create Queue Car
	@Bean
	public Queue userQueue() {
		return new Queue(AmqpConfig.TOPIC_USER);
	}
	
	//Create Queue User
	@Bean
	public Queue carQueue() {
		return new Queue(AmqpConfig.TOPIC_CAR);
	}
}
