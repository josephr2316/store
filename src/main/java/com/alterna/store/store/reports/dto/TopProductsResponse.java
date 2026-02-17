package com.alterna.store.store.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TopProductsResponse {
	private Long variantId;
	private String variantSku;
	private Long quantitySold;
	private BigDecimal revenue;
}
