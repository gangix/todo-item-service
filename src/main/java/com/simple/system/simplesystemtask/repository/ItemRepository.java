package com.simple.system.simplesystemtask.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simple.system.simplesystemtask.entity.Item;
import com.simple.system.simplesystemtask.entity.Status;

public interface ItemRepository extends JpaRepository<Item, Long> {
	Optional<Item> findById(Long id);

	List<Item> findByStatus(Status status);
}
