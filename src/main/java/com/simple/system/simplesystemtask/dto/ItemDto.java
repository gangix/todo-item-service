package com.simple.system.simplesystemtask.dto;

import java.time.Instant;

import com.simple.system.simplesystemtask.entity.Item;
import com.simple.system.simplesystemtask.entity.Status;

public record ItemDto(Long id, String description, Status status, Instant createdAt, String dueTime) {

	public static ItemDto from(Item item) {
		return new ItemDto(item.getId(), item.getDescription(), item.getStatus(), item.getCreatedAt(), item.getDueTime().toString());
	}
}