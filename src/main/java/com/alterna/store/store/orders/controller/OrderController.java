package com.alterna.store.store.orders.controller;

import com.alterna.store.store.orders.dto.OrderCreateRequest;
import com.alterna.store.store.orders.dto.OrderResponse;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	@Operation(summary = "Create order")
	public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get order by id")
	public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.getById(id));
	}

	@GetMapping
	@Operation(summary = "List orders (optional filter by status). Use status=PENDING|CONFIRMED|... or omit for all.")
	public ResponseEntity<List<OrderResponse>> list(@RequestParam(required = false) String status) {
		OrderStatus parsed = parseStatus(status);
		return ResponseEntity.ok(orderService.listByStatus(parsed));
	}

	private static OrderStatus parseStatus(String status) {
		if (status == null || status.isBlank()) return null;
		String s = status.trim().toUpperCase();
		if ("ALL".equals(s) || "TODOS".equals(s) || "TODAS".equals(s)) return null;
		try {
			return OrderStatus.valueOf(s);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
