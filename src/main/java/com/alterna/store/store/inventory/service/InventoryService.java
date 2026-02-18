package com.alterna.store.store.inventory.service;

import com.alterna.store.store.inventory.dto.InventoryBalanceResponse;
import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryBalanceRepository balanceRepository;

	@Transactional(readOnly = true)
	public InventoryBalanceResponse getByVariantId(Long variantId) {
		InventoryBalanceEntity b = balanceRepository.findByVariantId(variantId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory balance for variant", variantId));
		return toResponse(b);
	}

	@Transactional(readOnly = true)
	public List<InventoryBalanceResponse> listAll() {
		return balanceRepository.findAllWithVariant().stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public int getAvailableQuantity(Long variantId) {
		return balanceRepository.findByVariantId(variantId)
				.map(b -> Math.max(0, b.getQuantity() - b.getReserved()))
				.orElse(0);
	}

	private InventoryBalanceResponse toResponse(InventoryBalanceEntity b) {
		return InventoryBalanceResponse.builder()
				.id(b.getId())
				.variantId(b.getVariant().getId())
				.variantSku(b.getVariant().getSku())
				.quantity(b.getQuantity())
				.reserved(b.getReserved())
				.available(Math.max(0, b.getQuantity() - b.getReserved()))
				.build();
	}
}
