package com.simple.system.simplesystemtask.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.simple.system.simplesystemtask.config.RabbitMqConfig;

@Service
public class RabbitQueueServiceImpl implements RabbitQueueService {

	private final RabbitTemplate rabbitTemplate;

	public RabbitQueueServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void sendMessageToQueue(Long itemId) {
		this.rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.PAST_DUE_ROUTING_KEY, itemId);
	}
}
