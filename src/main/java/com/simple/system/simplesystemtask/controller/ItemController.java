package com.simple.system.simplesystemtask.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.simple.system.simplesystemtask.domain.AddItemCommand;
import com.simple.system.simplesystemtask.domain.UpdateDescriptionCommand;
import com.simple.system.simplesystemtask.domain.UpdateStatusCommand;
import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.model.AddItemRequest;
import com.simple.system.simplesystemtask.model.UpdateDescriptionRequest;
import com.simple.system.simplesystemtask.model.UpdateStatusRequest;
import com.simple.system.simplesystemtask.service.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping(path = "/{id}", consumes = {"*/*"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemDto> getItem(@PathVariable(name = "id", required = true) Long id) {
		return ResponseEntity.ok(itemService.getItemById(id));
	}

	@GetMapping(consumes = {"*/*"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ItemDto>> getItemList(@RequestParam(name = "notDone", required = false) boolean notDone) {
		if (notDone) {
			return ResponseEntity.ok(itemService.getAllNotDoneItems());
		}
		return ResponseEntity.ok(itemService.getAllItems());
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemDto> addItem(@Valid @RequestBody(required = true) AddItemRequest request) {
		var addItemCommand = new AddItemCommand(request.description(), Status.valueOf(request.status()),
				request.dueTime());
		ItemDto item = itemService.addItem(addItemCommand);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(item.id()).toUri();
		return ResponseEntity.created(location).body(item);
	}

	@PatchMapping(path = "/{id}/description", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void updateDescription(@PathVariable(name = "id", required = true) Long id,
			@Valid @RequestBody UpdateDescriptionRequest request) {
		var updateDescriptionCommand = new UpdateDescriptionCommand(id, request.description());
		itemService.updateDescription(updateDescriptionCommand);
	}

	@PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void updateStatus(@PathVariable(name = "id", required = true) Long id,
			@Valid @RequestBody UpdateStatusRequest request) {
		var updateStatusCommand = new UpdateStatusCommand(id, Status.valueOf(request.status()));
		itemService.updateStatus(updateStatusCommand, true);
	}
}
