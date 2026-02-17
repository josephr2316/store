package com.alterna.store.store.catalog.dto;

import lombok.Data;

@Data
public class ProductUpdateRequest {
	private String name;
	private String description;
	private String sku;
}
