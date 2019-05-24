package com.example.springbootexample.insurance.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.example.springbootexample.insurance.AmqpConfig;

public class MessageReceiver  {

	Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
	
	@RabbitListener(queues = {AmqpConfig.TOPIC_CAR})
	public void onReceiveMessageFromCarTopic(String message) {
		logger.info(String.format("Received Car Message: %s", message));
	}
	
	@RabbitListener(queues = {AmqpConfig.TOPIC_USER})
	public void onReceiveMessageFromUserTopic(String message) {
		logger.info(String.format("Received User Message: %s", message));
	}

}
