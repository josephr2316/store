package com.alterna.store.store.orders.controller;

import com.alterna.store.store.orders.dto.OrderAddressUpdateRequest;
import com.alterna.store.store.orders.dto.OrderResponse;
import com.alterna.store.store.orders.service.OrderAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/address")
@RequiredArgsConstructor
@Tag(name = "Orders - Address")
public class OrderAddressController {

	private final OrderAddressService addressService;

	@PatchMapping
	@Operation(summary = "Update shipping address")
	public ResponseEntity<OrderResponse> updateAddress(@PathVariable Long orderId,
			@RequestBody OrderAddressUpdateRequest request) {
		return ResponseEntity.ok(addressService.updateAddress(orderId, request));
	}
}
