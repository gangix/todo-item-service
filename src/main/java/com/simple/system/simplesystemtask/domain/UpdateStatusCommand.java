package com.simple.system.simplesystemtask.domain;

import com.simple.system.simplesystemtask.entity.Status;

public record UpdateStatusCommand(Long id, Status status) {}