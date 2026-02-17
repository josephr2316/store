package com.alterna.store.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCreateRequest {
	@NotBlank
	private String name;
	private String description;
	private String sku;
}
