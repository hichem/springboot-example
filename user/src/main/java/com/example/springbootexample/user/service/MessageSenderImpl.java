package com.example.springbootexample.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springbootexample.user.AmqpConfig;

@Service
public class MessageSenderImpl implements MessageSender {

	//Initialize logger
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RabbitTemplate template;
	
	
	public void sendMessage(String message) {
		logger.info(String.format("[sendMessage] Message: %s", message));
		
		this.template.convertAndSend(AmqpConfig.TOPIC_USER, message);
		
	}
	
	public void sendObject(Object object) {
		logger.info(String.format("[sendObject] Object: %s", object.toString()));
		
		this.template.convertAndSend(AmqpConfig.TOPIC_USER, object);
		
	}
}
