package com.alterna.store.store.orders.controller;

import com.alterna.store.store.orders.dto.OrderResponse;
import com.alterna.store.store.orders.dto.OrderTransitionRequest;
import com.alterna.store.store.orders.service.OrderTransitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/transition")
@RequiredArgsConstructor
@Tag(name = "Orders - Transitions")
public class OrderTransitionController {

	private final OrderTransitionService transitionService;

	@PostMapping
	@Operation(summary = "Transition order status (e.g. PENDING -> CONFIRMED)")
	public ResponseEntity<OrderResponse> transition(@PathVariable Long orderId,
			@Valid @RequestBody OrderTransitionRequest request) {
		return ResponseEntity.ok(transitionService.transition(orderId, request));
	}
}
