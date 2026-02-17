package com.alterna.store.store.inventory.service;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.inventory.dto.InventoryAdjustmentCreateRequest;
import com.alterna.store.store.inventory.dto.InventoryAdjustmentResponse;
import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.entity.InventoryAdjustmentEntity;
import com.alterna.store.store.inventory.enums.AdjustmentReason;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.inventory.repository.InventoryAdjustmentRepository;
import com.alterna.store.store.shared.exception.ConflictException;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryAdjustmentService {

	private final InventoryBalanceRepository balanceRepository;
	private final InventoryAdjustmentRepository adjustmentRepository;
	private final VariantRepository variantRepository;

	@Transactional
	public InventoryAdjustmentResponse create(InventoryAdjustmentCreateRequest req) {
		VariantEntity variant = variantRepository.findById(req.getVariantId())
				.orElseThrow(() -> new ResourceNotFoundException("Variant", req.getVariantId()));
		InventoryBalanceEntity balance = balanceRepository.findByVariantId(req.getVariantId())
				.orElseGet(() -> {
					InventoryBalanceEntity b = InventoryBalanceEntity.builder().variant(variant).quantity(0).reserved(0).build();
					return balanceRepository.save(b);
				});
		int newQty = balance.getQuantity() + req.getQuantityDelta();
		if (newQty < 0) {
			throw new ConflictException("Adjustment would result in negative quantity");
		}
		if (newQty < balance.getReserved()) {
			throw new ConflictException("Quantity cannot be less than reserved");
		}
		balance.setQuantity(newQty);
		balanceRepository.save(balance);
		InventoryAdjustmentEntity adj = InventoryAdjustmentEntity.builder()
				.variant(variant)
				.quantityDelta(req.getQuantityDelta())
				.reason(req.getReason())
				.note(req.getNote())
				.build();
		adj = adjustmentRepository.save(adj);
		return InventoryAdjustmentResponse.builder()
				.id(adj.getId())
				.variantId(adj.getVariant().getId())
				.quantityDelta(adj.getQuantityDelta())
				.reason(adj.getReason())
				.note(adj.getNote())
				.createdAt(adj.getCreatedAt())
				.createdBy(adj.getCreatedBy())
				.build();
	}
}
