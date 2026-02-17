package com.alterna.store.store.inventory.repository;

import com.alterna.store.store.inventory.entity.InventoryAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustmentEntity, Long> {
	List<InventoryAdjustmentEntity> findByVariantIdOrderByCreatedAtDesc(Long variantId, org.springframework.data.domain.Pageable pageable);
}
