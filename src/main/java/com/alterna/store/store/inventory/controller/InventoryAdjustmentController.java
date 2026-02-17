package com.alterna.store.store.inventory.controller;

import com.alterna.store.store.inventory.dto.InventoryAdjustmentCreateRequest;
import com.alterna.store.store.inventory.dto.InventoryAdjustmentResponse;
import com.alterna.store.store.inventory.service.InventoryAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/adjustments")
@RequiredArgsConstructor
@Tag(name = "Inventory - Adjustments")
public class InventoryAdjustmentController {

	private final InventoryAdjustmentService adjustmentService;

	@PostMapping
	@Operation(summary = "Create adjustment")
	public ResponseEntity<InventoryAdjustmentResponse> create(@Valid @RequestBody InventoryAdjustmentCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adjustmentService.create(request));
	}
}
