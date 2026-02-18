package com.alterna.store.store.reports.repository;

import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReportsRepository extends JpaRepository<com.alterna.store.store.orders.entity.OrderEntity, Long> {

	@Query("SELECT COUNT(o), COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to")
	Object[] deliveredOrdersCountAndTotal(@Param("from") Instant from, @Param("to") Instant to, @Param("status") OrderStatus status);

	/** Returns [variantId, variantSku, totalQty, totalRevenue] to avoid N+1 SKU lookups. */
	@Query("SELECT oi.variant.id, oi.variant.sku, SUM(oi.quantity), SUM(oi.quantity * oi.unitPrice) FROM OrderItemEntity oi JOIN oi.order o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to GROUP BY oi.variant.id, oi.variant.sku ORDER BY SUM(oi.quantity) DESC")
	List<Object[]> topVariantsByQuantity(@Param("from") Instant from, @Param("to") Instant to, @Param("status") OrderStatus status, org.springframework.data.domain.Pageable pageable);

	@Query(value = "SELECT CAST(o.created_at AS DATE) AS sale_date, COUNT(*), COALESCE(SUM(o.total_amount), 0) FROM orders o WHERE o.status = :status AND o.created_at >= :from AND o.created_at < :to GROUP BY CAST(o.created_at AS DATE) ORDER BY sale_date", nativeQuery = true)
	List<Object[]> deliveredOrdersCountAndTotalByDay(@Param("from") Instant from, @Param("to") Instant to, @Param("status") OrderStatus status);

	@Query("SELECT COUNT(DISTINCT oi.variant.id) FROM OrderItemEntity oi JOIN oi.order o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to")
	long countDistinctVariantsSold(@Param("from") Instant from, @Param("to") Instant to, @Param("status") OrderStatus status);
}
