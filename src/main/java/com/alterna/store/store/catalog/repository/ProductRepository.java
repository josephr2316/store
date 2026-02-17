package com.alterna.store.store.catalog.repository;

import com.alterna.store.store.catalog.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	Optional<ProductEntity> findBySku(String sku);
	boolean existsBySku(String sku);
}
