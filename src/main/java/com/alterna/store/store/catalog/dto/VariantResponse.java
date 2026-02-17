package com.alterna.store.store.catalog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class VariantResponse {
	private Long id;
	private Long productId;
	private String name;
	private String sku;
	private Map<String, String> attributes;
	private Instant createdAt;
}
