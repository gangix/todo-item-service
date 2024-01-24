package com.simple.system.simplesystemtask.model;

import jakarta.validation.constraints.NotEmpty;

public record UpdateDescriptionRequest(@NotEmpty(message = "Description is required") String description) {}
