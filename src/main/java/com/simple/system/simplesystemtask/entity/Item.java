package com.simple.system.simplesystemtask.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@CreationTimestamp
	private Instant createdAt;

	private Instant dueTime;

	private Instant doneAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getDueTime() {
		return dueTime;
	}

	public void setDueTime(Instant dueTime) {
		this.dueTime = dueTime;
	}

	public Instant getDoneAt() {
		return doneAt;
	}

	public void setDoneAt(Instant doneAt) {
		this.doneAt = doneAt;
	}
}
