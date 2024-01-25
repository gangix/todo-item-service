package com.simple.system.simplesystemtask.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	public static final String EXCHANGE = "item_service_exchange";

	public static final String PAST_DUE_ROUTING_KEY = "item_service_past_due";

	public static final String PAST_DUE_PROCESS_QUEUE = "past_due_process";

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		return rabbitTemplate;
	}

	@Bean
	TopicExchange topicExchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	Queue mainQueue() {
		return QueueBuilder.durable(PAST_DUE_PROCESS_QUEUE).build();
	}

	@Bean
	Binding bindingMessages() {
		return BindingBuilder.bind(mainQueue()).to(topicExchange()).with(PAST_DUE_ROUTING_KEY);
	}

	@Bean
	public Jackson2JsonMessageConverter jacksonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public MessageConverter messageConverter() {
		return new SimpleMessageConverter();
	}
}