package com.simple.system.simplesystemtask.job;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.service.ItemService;
import com.simple.system.simplesystemtask.service.RabbitQueueService;

@Component
@Profile(value = { "default" })
public class PublishItemsToProcessAsPastDue {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublishItemsToProcessAsPastDue.class);

	private final ItemService itemService;

	private final RabbitQueueService rabbitQueueService;

	public PublishItemsToProcessAsPastDue(ItemService itemService, RabbitQueueService rabbitQueueService) {
		this.itemService = itemService;
		this.rabbitQueueService = rabbitQueueService;
	}

	@Scheduled(cron = "${job.pastDue.cron:0 */5 * * * *}")
	public void publisItemsToMarkAsPastDue() {
		LOGGER.info("publisItemsToMarkAsPastDue started");
		StopWatch stopWatch = new StopWatch("publisItemsToMarkAsPastDue");
		stopWatch.start();
		List<ItemDto> items = itemService.getItemsToMarkAsPastDue();
		for (ItemDto item : items) {
			rabbitQueueService.sendMessageToQueue(item.id());
		}
		stopWatch.stop();
		LOGGER.info("Time taken to get {} items,  {} : {}", items.size(), stopWatch.getMessage(),
				stopWatch.getTime(TimeUnit.SECONDS));
	}
}