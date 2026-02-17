package com.alterna.store.store.catalog.mapper;

import com.alterna.store.store.catalog.dto.ProductResponse;
import com.alterna.store.store.catalog.dto.VariantResponse;
import com.alterna.store.store.catalog.entity.ProductEntity;
import com.alterna.store.store.catalog.entity.VariantEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

	public ProductResponse toResponse(ProductEntity e, boolean includeVariants) {
		return ProductResponse.builder()
				.id(e.getId())
				.name(e.getName())
				.description(e.getDescription())
				.sku(e.getSku())
				.createdAt(e.getCreatedAt())
				.variants(includeVariants && e.getVariants() != null
						? e.getVariants().stream().map(this::toVariantResponse).collect(Collectors.toList())
						: Collections.emptyList())
				.build();
	}

	public VariantResponse toVariantResponse(VariantEntity e) {
		return VariantResponse.builder()
				.id(e.getId())
				.productId(e.getProduct() != null ? e.getProduct().getId() : null)
				.name(e.getName())
				.sku(e.getSku())
				.attributes(e.getAttributes())
				.createdAt(e.getCreatedAt())
				.build();
	}
}
