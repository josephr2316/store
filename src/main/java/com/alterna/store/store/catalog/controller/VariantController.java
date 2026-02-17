package com.alterna.store.store.catalog.controller;

import com.alterna.store.store.catalog.dto.VariantCreateRequest;
import com.alterna.store.store.catalog.dto.VariantResponse;
import com.alterna.store.store.catalog.dto.VariantUpdateRequest;
import com.alterna.store.store.catalog.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variants")
@RequiredArgsConstructor
@Tag(name = "Catalog - Variants")
public class VariantController {

	private final VariantService variantService;

	@PostMapping
	@Operation(summary = "Create variant")
	public ResponseEntity<VariantResponse> create(@Valid @RequestBody VariantCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(variantService.create(request));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get variant by id")
	public ResponseEntity<VariantResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(variantService.getById(id));
	}

	@GetMapping("/by-product/{productId}")
	@Operation(summary = "List variants by product")
	public ResponseEntity<List<VariantResponse>> listByProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(variantService.findByProductId(productId));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update variant")
	public ResponseEntity<VariantResponse> update(@PathVariable Long id,
			@Valid @RequestBody VariantUpdateRequest request) {
		return ResponseEntity.ok(variantService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete variant")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		variantService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
