package com.alterna.store.store.orders.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
	private Long id;
	private Long variantId;
	private String variantSku;
	private Integer quantity;
	private BigDecimal unitPrice;
}
