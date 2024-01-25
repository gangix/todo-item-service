package com.simple.system.simplesystemtask.service;

public interface RabbitQueueService {

	void sendMessageToQueue(Long itemId);

}