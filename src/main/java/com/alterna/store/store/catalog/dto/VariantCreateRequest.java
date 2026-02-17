package com.alterna.store.store.catalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class VariantCreateRequest {
	@NotNull
	private Long productId;
	@NotBlank
	private String name;
	private String sku;
	private Map<String, String> attributes;
}
