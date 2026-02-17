package com.alterna.store.store.catalog.dto;

import lombok.Data;

import java.util.Map;

@Data
public class VariantUpdateRequest {
	private String name;
	private String sku;
	private Map<String, String> attributes;
}
