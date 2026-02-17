package com.alterna.store.store.catalog.controller;

import com.alterna.store.store.catalog.dto.ProductCreateRequest;
import com.alterna.store.store.catalog.dto.ProductResponse;
import com.alterna.store.store.catalog.dto.ProductUpdateRequest;
import com.alterna.store.store.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Catalog - Products")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@Operation(summary = "Create product")
	public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get product by id")
	public ResponseEntity<ProductResponse> getById(@PathVariable Long id,
			@RequestParam(defaultValue = "true") boolean includeVariants) {
		return ResponseEntity.ok(productService.getById(id, includeVariants));
	}

	@GetMapping
	@Operation(summary = "List products")
	public ResponseEntity<List<ProductResponse>> list(
			@RequestParam(defaultValue = "true") boolean includeVariants) {
		return ResponseEntity.ok(productService.findAll(includeVariants));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update product")
	public ResponseEntity<ProductResponse> update(@PathVariable Long id,
			@Valid @RequestBody ProductUpdateRequest request) {
		return ResponseEntity.ok(productService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete product")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
