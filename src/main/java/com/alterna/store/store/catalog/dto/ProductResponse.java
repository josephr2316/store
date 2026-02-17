package com.alterna.store.store.catalog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ProductResponse {
	private Long id;
	private String name;
	private String description;
	private String sku;
	private Instant createdAt;
	private List<VariantResponse> variants;
}
