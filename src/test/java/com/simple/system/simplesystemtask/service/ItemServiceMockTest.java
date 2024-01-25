package com.simple.system.simplesystemtask.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simple.system.simplesystemtask.domain.AddItemCommand;
import com.simple.system.simplesystemtask.domain.UpdateStatusCommand;
import com.simple.system.simplesystemtask.domain.exception.OperationIsForbidenException;
import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.entity.Item;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
public class ItemServiceMockTest {

	@InjectMocks
	private ItemServiceImpl itemService;

	@Mock
	private ItemRepository itemRepository;

	@Captor
	ArgumentCaptor<Item> itemCaptor;

	@Test
	void GivenEuropeanTimeZone_ShouldBeSavedInUTCZoneCorrectlyWithOtherParams() {
		ZonedDateTime zdt = ZonedDateTime.of(2024, 4, 3, 04, 05, 05, 0,
				ZoneId.ofOffset("UTC", ZoneOffset.of("+02:00")));
		Instant instant = zdt.toInstant();

		try (MockedStatic<ItemDto> itemDto = Mockito.mockStatic(ItemDto.class)) {
			itemDto.when(() -> ItemDto.from(any())).thenReturn(null);
			AddItemCommand cmd = new AddItemCommand("desc", Status.NOT_DONE, instant.toString());
			itemService.addItem(cmd);
		}
		
		Mockito.verify(itemRepository, times(1)).save(itemCaptor.capture());
		Item value = itemCaptor.getValue();
		
		ZonedDateTime zdtExpected = ZonedDateTime.of(2024, 4, 3, 02, 05, 05, 0, ZoneId.of("UTC"));
		Instant instantExpected = zdtExpected.toInstant();
		assertThat(value.getDueTime()).isEqualTo(instantExpected);
		assertThat(value.getDescription()).isEqualTo("desc");	
		assertThat(value.getStatus()).isEqualTo(Status.NOT_DONE);
		assertThat(value.getDoneAt()).isNull();
	}
	
	@Test
	void GivenUpdateStatusCommandAsNotDone_ShouldBeSavedProperly() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.NOT_DONE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.DONE);
		
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		itemService.updateStatus(cmd, true);
		Mockito.verify(itemRepository, times(1)).save(itemCaptor.capture());
		Item value = itemCaptor.getValue();
	
		assertThat(value.getStatus()).isEqualTo(Status.NOT_DONE);
	}
	
	@Test
	void GivenUpdateStatusCommandAsDone_ShouldBeSavedProperly() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.DONE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.NOT_DONE);
		
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		itemService.updateStatus(cmd, true);
		Mockito.verify(itemRepository, times(1)).save(itemCaptor.capture());
		Item value = itemCaptor.getValue();
	
		assertThat(value.getStatus()).isEqualTo(Status.DONE);
	}
	
	@Test
	void WhenItemIsPastDue_GivenUpdateStatusCommandFromWeb_ShouldThrowException() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.DONE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.PAST_DUE);
		
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		OperationIsForbidenException thrown = assertThrows(
				OperationIsForbidenException.class,
		           () -> itemService.updateStatus(cmd, true), "Expected updateStatus() to throw, but it didn't"
		    );
		assertThat(thrown.getMessage()).isEqualTo("Updating PAST_DUE item is forbidden");
	}
	
	@Test
	void GivenUpdateStatusCommandWithPastDueFromWeb_ShouldThrowException() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.PAST_DUE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.DONE);
		
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		OperationIsForbidenException thrown = assertThrows(
				OperationIsForbidenException.class,
		           () -> itemService.updateStatus(cmd, true), "Expected updateStatus() to throw, but it didn't"
		    );
		assertThat(thrown.getMessage()).isEqualTo("Updating status as PAST_DUE is forbidden");
	}
	
	@Test
	void WhenItemIsNotDone_GivenUpdateStatusCommandFromJob_ShouldBeSavedProperly() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.PAST_DUE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.NOT_DONE);
		
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		itemService.updateStatus(cmd, false);
		Mockito.verify(itemRepository, times(1)).save(itemCaptor.capture());
		Item value = itemCaptor.getValue();
	
		assertThat(value.getStatus()).isEqualTo(Status.PAST_DUE);
	}
	
	@Test
	void GivenUpdateStatusCommandWithPastDueFromJob_ShouldBeSaved() {
		UpdateStatusCommand cmd = new UpdateStatusCommand(1L, Status.PAST_DUE);
		Item item = new Item();
		item.setDescription("desc");
		item.setDueTime(Instant.now());
		item.setStatus(Status.DONE);
		Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		
		OperationIsForbidenException thrown = assertThrows(
				OperationIsForbidenException.class,
		           () -> itemService.updateStatus(cmd, false), "Expected updateStatus() to throw, but it didn't"
		    );
		assertThat(thrown.getMessage()).isEqualTo("Updating status as PAST_DUE when current status is different than NOT_DONE is forbidden");
	}
}