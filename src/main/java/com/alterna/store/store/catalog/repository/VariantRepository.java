package com.alterna.store.store.catalog.repository;

import com.alterna.store.store.catalog.entity.VariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<VariantEntity, Long> {
	List<VariantEntity> findByProductId(Long productId);
	Optional<VariantEntity> findBySku(String sku);
	boolean existsBySku(String sku);
}
