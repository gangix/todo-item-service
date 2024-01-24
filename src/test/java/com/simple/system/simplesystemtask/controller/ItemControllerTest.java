package com.simple.system.simplesystemtask.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.simple.system.simplesystemtask.domain.AddItemCommand;
import com.simple.system.simplesystemtask.dto.ItemDto;
import com.simple.system.simplesystemtask.entity.Status;
import com.simple.system.simplesystemtask.repository.ItemRepository;
import com.simple.system.simplesystemtask.service.ItemService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class ItemControllerTest {
	@LocalServerPort
	private Integer port;

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ItemRepository itemRepository;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
		itemRepository.deleteAll();
	}

	@Test
	void GivenProperInput_ShouldCreateItemSuccessfully() {
		given().contentType(ContentType.JSON).body("""
				  {
				      "description": "Add Item",
				      "status": "NOT_DONE",
				      "dueTime": "2024-10-07T12:06:56.568+01:00"
				  }
				""").when().post("/api/items").then().statusCode(201)
				.header("Location", matchesRegex(".*/api/items/[0-9]+$")).body("id", notNullValue())
				.body("description", equalTo("Add Item")).body("dueTime", equalTo("2024-10-07T11:06:56.568Z"))
				.body("createdAt", notNullValue()).body("doneAt", nullValue());
	}

	@Test
	void GivenNullDescriptionInput_ShouldGetBadRequestException() {
		given().contentType(ContentType.JSON).body("""
				  {
				      "status": "NOT_DONE",
				      "dueTime": "2024-10-07T12:06:56.568+01:00"
				  }
				""").when().post("/api/items").then().statusCode(400).body("errors",
				hasItem("Description is required"));
	}

	@Test
	void GivenDescription_ShouldUpdateDescriptionSuccessfully() {
		AddItemCommand addItemCommand = new AddItemCommand("Add Item1", Status.NOT_DONE,
				Instant.now().plus(10, ChronoUnit.MINUTES).toString());

		ItemDto item = itemService.addItem(addItemCommand);
		given().contentType(ContentType.JSON).body("""
				  {
				      "description": "Add Item Test"
				  }
				""").when().patch("/api/items/{id}/description", item.id()).then().statusCode(200);
		assertThat(itemService.getItemById(item.id()).description()).isEqualTo("Add Item Test");
	}

	@Test
	void GivenDONEStatus_ShouldUpdateStatusSuccessfully() {
		AddItemCommand addItemCommand = new AddItemCommand("Add Item1", Status.NOT_DONE,
				Instant.now().plus(10, ChronoUnit.MINUTES).toString());

		ItemDto item = itemService.addItem(addItemCommand);
		given().contentType(ContentType.JSON).body("""
				  {
				      "status": "DONE"
				  }
				""").when().patch("/api/items/{id}/status", item.id()).then().statusCode(200);
		assertThat(itemService.getItemById(item.id()).status()).isEqualTo(Status.DONE);
	}

	@Test
	void GivenNOT_DONEStatus_ShouldUpdateStatusSuccessfully() {
		AddItemCommand addItemCommand = new AddItemCommand("Add Item1", Status.DONE,
				Instant.now().plus(10, ChronoUnit.MINUTES).toString());

		ItemDto item = itemService.addItem(addItemCommand);
		given().contentType(ContentType.JSON).body("""
				  {
				      "status": "NOT_DONE"
				  }
				""").when().patch("/api/items/{id}/status", item.id()).then().statusCode(200);
		assertThat(itemService.getItemById(item.id()).status()).isEqualTo(Status.NOT_DONE);
	}

	@Test
	void GivenStatus_WhenUpdatingPastDueItem_ShouldGetForbiddenException() {
		AddItemCommand addItemCommand = new AddItemCommand("Add Item1", Status.PAST_DUE,
				Instant.now().plus(10, ChronoUnit.MINUTES).toString());

		ItemDto item = itemService.addItem(addItemCommand);
		given().contentType(ContentType.JSON).body("""
				  {
				      "status": "DONE"
				  }
				""").when().patch("/api/items/{id}/status", item.id()).then().statusCode(403).body("errors",
				hasItem("Updating PAST_DUE item is forbidden"));
	}

	@Test
	public void GivenId_ShouldGetDetailsOfSpecificItem() {
		ItemDto item = createItem(Status.NOT_DONE);

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", "application/json");

		given().headers(headers).when().get("/api/items/{id}", item.id()).then().statusCode(200)
				.body("status", equalTo("NOT_DONE")).body("description", equalTo("Add Item1"));
	}

	@Test
	public void GivenNotDoneFilterTrue_ShouldGetAllNotDoneItems() {
		for (int i = 1; i < 11; i++) {
			createItem(Status.NOT_DONE);
		}
		createItem(Status.PAST_DUE);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", "application/json");
		given().headers(headers).queryParam("notDone", true).when().get("/api/items").then()
		.statusCode(200)
				.body("id", hasSize(10));
	}
	
	@Test
	public void GivenNotDoneFilterFalse_ShouldGetAllItems() {
		for (int i = 1; i < 11; i++) {
			createItem(Status.NOT_DONE);
		}
		createItem(Status.PAST_DUE);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", "application/json");
		given().headers(headers).when().get("/api/items").then()
		.statusCode(200)
				.body("id", hasSize(11));
	}

	private ItemDto createItem(Status status) {
		Instant dueTime = Instant.now().plus(10, ChronoUnit.MINUTES);
		AddItemCommand addItemCommand = new AddItemCommand("Add Item1", status, dueTime.toString());
		ItemDto item = itemService.addItem(addItemCommand);
		return item;
	}
}