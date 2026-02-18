package com.alterna.store.store.inventory.repository;

import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalanceEntity, Long> {

	Optional<InventoryBalanceEntity> findByVariantId(Long variantId);

	/** Fetch all balances with variant eagerly to avoid N+1. */
	@Query("SELECT b FROM InventoryBalanceEntity b JOIN FETCH b.variant")
	List<InventoryBalanceEntity> findAllWithVariant();
}
