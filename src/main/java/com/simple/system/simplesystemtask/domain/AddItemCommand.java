package com.simple.system.simplesystemtask.domain;

import com.simple.system.simplesystemtask.entity.Status;

public record AddItemCommand(String description, Status status, String dueTime) {}