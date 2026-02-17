package com.alterna.store.store.inventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryBalanceResponse {
	private Long id;
	private Long variantId;
	private String variantSku;
	private Integer quantity;
	private Integer reserved;
	private Integer available;
}
