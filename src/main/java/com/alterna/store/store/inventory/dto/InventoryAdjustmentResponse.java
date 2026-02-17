package com.alterna.store.store.inventory.dto;

import com.alterna.store.store.inventory.enums.AdjustmentReason;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class InventoryAdjustmentResponse {
	private Long id;
	private Long variantId;
	private Integer quantityDelta;
	private AdjustmentReason reason;
	private String note;
	private Instant createdAt;
	private String createdBy;
}
