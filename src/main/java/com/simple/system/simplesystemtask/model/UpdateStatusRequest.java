package com.simple.system.simplesystemtask.model;

import com.simple.system.simplesystemtask.entity.Status;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
		@NotNull(message = "Status is required") 
		@ValueOfEnum(enumClass = Status.class) 
		String status) {}
