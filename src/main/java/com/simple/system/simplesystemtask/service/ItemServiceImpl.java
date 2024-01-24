package com.simple.system.simplesystemtask.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simple.system.simplesystemtask.domain.AddItemCommand;
import com.simple.system.simplesystemtask.domain.UpdateDescriptionCommand;
import com.simple.system.simplesystemtask.domain.UpdateStatusCommand;
import com.simple.system.simplesystemtask.domain.exception.ItemNotFoundException;
import com.simple.system.simplesystemtask.domain.exception.OperationIsForbidenException;
import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.entity.Item;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.repository.ItemRepository;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;

	public ItemServiceImpl(ItemRepository itemRepository) {
		super();
		this.itemRepository = itemRepository;
	}

	@Override
	@Transactional
	public ItemDto addItem(AddItemCommand addItemCommand) {
		var item = new Item();
		item.setDescription(addItemCommand.description());
		item.setDueTime(Instant.parse(addItemCommand.dueTime()));
		item.setStatus(addItemCommand.status());
		if (Status.DONE == addItemCommand.status()) {
			item.setDoneAt(Instant.now());
		}
		return ItemDto.from(itemRepository.save(item));
	}

	@Override
	@Transactional
	public void updateDescription(UpdateDescriptionCommand updateDescriptionCommand) {
		var item = itemRepository.findById(updateDescriptionCommand.id())
				.orElseThrow(() -> ItemNotFoundException.of(updateDescriptionCommand.id()));
		item.setDescription(updateDescriptionCommand.description());
		itemRepository.save(item);
	}

	@Override
	@Transactional
	public void updateStatus(UpdateStatusCommand updateStatusCommand) {
		Status status = updateStatusCommand.status();
		Item item = getItemEntityById(updateStatusCommand.id());
		if (Status.PAST_DUE == item.getStatus()) {
			throw new OperationIsForbidenException("Updating PAST_DUE item");
		}

		switch (status) {
			case DONE -> markAsDone(item);
			case NOT_DONE -> markAsNotDone(item);
			case PAST_DUE -> throw new OperationIsForbidenException("Updating status as PAST_DUE");
		}
	}

	private void markAsDone(Item item) {
		item.setStatus(Status.DONE);
		item.setDoneAt(Instant.now());

		itemRepository.save(item);
	}

	private void markAsNotDone(Item item) {
		item.setStatus(Status.NOT_DONE);
		item.setDoneAt(null);

		itemRepository.save(item);
	}

	@Override
	public ItemDto getItemById(Long id) {
		return ItemDto.from(getItemEntityById(id));
	}
	
	@Override
	public List<ItemDto> getAllNotDoneItems() {
		return itemRepository.findByStatus(Status.NOT_DONE).stream().map(ItemDto::from).toList();
	}

	@Override
	public List<ItemDto> getAllItems() {
		return itemRepository.findAll().stream().map(ItemDto::from).toList();
	}
	
	private Item getItemEntityById(Long id) {
		return itemRepository.findById(id).orElseThrow(() -> ItemNotFoundException.of(id));
	}
}