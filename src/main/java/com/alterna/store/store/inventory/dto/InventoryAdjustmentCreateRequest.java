package com.alterna.store.store.inventory.dto;

import com.alterna.store.store.inventory.enums.AdjustmentReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryAdjustmentCreateRequest {
	@NotNull
	private Long variantId;
	@NotNull
	private Integer quantityDelta;
	@NotNull
	private AdjustmentReason reason;
	private String note;
}
