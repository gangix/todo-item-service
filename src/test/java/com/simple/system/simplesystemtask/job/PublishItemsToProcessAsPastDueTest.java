package com.simple.system.simplesystemtask.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.service.ItemService;
import com.simple.system.simplesystemtask.service.RabbitQueueService;

@ExtendWith(MockitoExtension.class)
class PublishItemsToProcessAsPastDueTest {
	
	@InjectMocks
	private PublishItemsToProcessAsPastDue publishItemsToProcessAsPastDue;
	
	@Mock
	private RabbitQueueService rabbitQueueService;

	@Mock
	private ItemService itemService;

	@Test
	void publisItemsToMarkAsPastDueTest() {
		ItemDto item1 = new ItemDto(1L, "desc", Status.NOT_DONE, Instant.now(),
				Instant.now().minus(1, ChronoUnit.SECONDS).toString());
		ItemDto item2 = new ItemDto(2L, "desc", Status.NOT_DONE, Instant.now(),
				Instant.now().minus(2, ChronoUnit.SECONDS).toString());
		when(itemService.getItemsToMarkAsPastDue()).thenReturn(List.of(item1, item2));

		publishItemsToProcessAsPastDue.publisItemsToMarkAsPastDue();

		verify(rabbitQueueService, times(1)).sendMessageToQueue(1L);
		verify(rabbitQueueService, times(1)).sendMessageToQueue(2L);
	}
}