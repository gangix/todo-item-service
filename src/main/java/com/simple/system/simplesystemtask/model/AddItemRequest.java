package com.simple.system.simplesystemtask.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simple.system.simplesystemtask.entity.Status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(@NotEmpty(message = "Description is required") String description,
		@NotNull(message = "Status is required") 
		@ValueOfEnum(enumClass = Status.class) 
		String status,
		@NotNull(message = "Due time is required") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ") String dueTime){
}
