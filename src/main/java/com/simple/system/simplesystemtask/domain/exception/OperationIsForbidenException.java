package com.simple.system.simplesystemtask.domain.exception;

public class OperationIsForbidenException extends RuntimeException {
	public OperationIsForbidenException(String operation) {
		super(String.format("%s is forbidden", operation));
	}

	public static OperationIsForbidenException of(String operation) {
		return new OperationIsForbidenException(operation);
	}
}