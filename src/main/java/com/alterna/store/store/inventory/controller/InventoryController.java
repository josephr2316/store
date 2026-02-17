package com.alterna.store.store.inventory.controller;

import com.alterna.store.store.inventory.dto.InventoryBalanceResponse;
import com.alterna.store.store.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	@GetMapping("/balance")
	@Operation(summary = "List all balances")
	public ResponseEntity<List<InventoryBalanceResponse>> listBalances() {
		return ResponseEntity.ok(inventoryService.listAll());
	}

	@GetMapping("/balance/variant/{variantId}")
	@Operation(summary = "Get balance by variant")
	public ResponseEntity<InventoryBalanceResponse> getByVariant(@PathVariable Long variantId) {
		return ResponseEntity.ok(inventoryService.getByVariantId(variantId));
	}

	@GetMapping("/available/{variantId}")
	@Operation(summary = "Get available quantity")
	public ResponseEntity<Integer> getAvailable(@PathVariable Long variantId) {
		return ResponseEntity.ok(inventoryService.getAvailableQuantity(variantId));
	}
}
