package com.simple.system.simplesystemtask.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.simple.system.simplesystemtask.config.RabbitMqConfig;
import com.simple.system.simplesystemtask.domain.UpdateStatusCommand;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.service.ItemService;

@Service
public class MarkItemsAsPastDueConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MarkItemsAsPastDueConsumer.class);

	private final ItemService itemService;

	public MarkItemsAsPastDueConsumer(ItemService itemService) {
		this.itemService = itemService;
	}

	@RabbitListener(queues = RabbitMqConfig.PAST_DUE_PROCESS_QUEUE)
	public void consumeJobApplication(Long id) {
		LOGGER.info("Incoming Message. messageId" + id);
		itemService.updateStatus(new UpdateStatusCommand(id, Status.PAST_DUE), false);
	}
}