package com.example.springbootexample.user;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springbootexample.user.service.MessageSender;
import com.example.springbootexample.user.service.MessageSenderImpl;



@Configuration
public class MyAppConfiguration {

	@Bean
	public Queue carQueue() {
		return new Queue(AmqpConfig.TOPIC_USER);
	}
	
	@Bean
	public MessageSender messageSender() {
		return new MessageSenderImpl();
	}
	
}
