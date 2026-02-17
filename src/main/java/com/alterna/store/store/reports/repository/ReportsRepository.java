package com.alterna.store.store.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReportsRepository extends JpaRepository<com.alterna.store.store.orders.entity.OrderEntity, Long> {

	@Query("""
			SELECT COUNT(o), COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o
			WHERE o.status = com.alterna.store.store.orders.enums.OrderStatus.DELIVERED
			AND o.createdAt BETWEEN :from AND :to
			""")
	Object[] deliveredOrdersCountAndTotal(@Param("from") Instant from, @Param("to") Instant to);

	@Query("""
			SELECT oi.variant.id, SUM(oi.quantity), SUM(oi.quantity * oi.unitPrice)
			FROM OrderItemEntity oi
			JOIN oi.order o
			WHERE o.status = com.alterna.store.store.orders.enums.OrderStatus.DELIVERED
			AND o.createdAt BETWEEN :from AND :to
			GROUP BY oi.variant.id
			ORDER BY SUM(oi.quantity) DESC
			""")
	List<Object[]> topVariantsByQuantity(@Param("from") Instant from, @Param("to") Instant to, org.springframework.data.domain.Pageable pageable);
}
