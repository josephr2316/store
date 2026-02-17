package com.alterna.store.store.inventory.repository;

import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalanceEntity, Long> {
	Optional<InventoryBalanceEntity> findByVariantId(Long variantId);
}
