package com.alterna.store.store.orders.controller;

import com.alterna.store.store.orders.dto.OrderHistoryResponse;
import com.alterna.store.store.orders.service.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/{orderId}/history")
@RequiredArgsConstructor
@Tag(name = "Orders - History")
public class OrderHistoryController {

	private final OrderHistoryService historyService;

	@GetMapping
	@Operation(summary = "Get order status history")
	public ResponseEntity<List<OrderHistoryResponse>> getHistory(@PathVariable Long orderId) {
		return ResponseEntity.ok(historyService.getHistory(orderId));
	}
}
