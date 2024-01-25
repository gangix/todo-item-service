package com.simple.system.simplesystemtask.service;

import java.util.List;

import com.simple.system.simplesystemtask.domain.AddItemCommand;
import com.simple.system.simplesystemtask.domain.UpdateDescriptionCommand;
import com.simple.system.simplesystemtask.domain.UpdateStatusCommand;
import com.simple.system.simplesystemtask.dto.ItemDto;

public interface ItemService {
	ItemDto addItem(AddItemCommand addItemCommand);

	void updateDescription(UpdateDescriptionCommand updateDescriptionCommand);

	void updateStatus(UpdateStatusCommand updateStatusCommand, boolean isWeb);

	ItemDto getItemById(Long id);
	
	List<ItemDto> getAllItems();

	List<ItemDto> getAllNotDoneItems();

	List<ItemDto> getItemsToMarkAsPastDue();
}
