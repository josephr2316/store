package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByStatus(OrderStatus status);

	/** All orders paginated, ordered by created date. */
	Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

	/** All orders (no pagination — kept for backward compat but avoid for large datasets). */
	List<OrderEntity> findAllByOrderByCreatedAtDesc();

	/** Orders by status, paginated, ordered by created date. */
	Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

	/** Orders by status ordered by created date (no fetch). */
	List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);

	List<OrderEntity> findByCreatedAtBetween(Instant from, Instant to);

	/**
	 * List order summaries via native SQL (no entity load, no lazy collections).
	 * Sort/pagination must be applied via Pageable (Sort.by(DESC, "created_at")) to avoid duplicate ORDER BY.
	 */
	@Query(value = "SELECT id, external_id, channel, status, customer_name, customer_phone, customer_email, "
			+ "shipping_address, shipping_city, shipping_region, shipping_postal_code, total_amount, currency, notes, created_at "
			+ "FROM orders /* #pageable */",
			countQuery = "SELECT COUNT(*) FROM orders",
			nativeQuery = true)
	Page<Object[]> findAllSummariesNative(Pageable pageable);

	@Query(value = "SELECT id, external_id, channel, status, customer_name, customer_phone, customer_email, "
			+ "shipping_address, shipping_city, shipping_region, shipping_postal_code, total_amount, currency, notes, created_at "
			+ "FROM orders WHERE status = CAST(:status AS VARCHAR) /* #pageable */",
			countQuery = "SELECT COUNT(*) FROM orders WHERE status = CAST(:status AS VARCHAR)",
			nativeQuery = true)
	Page<Object[]> findSummariesByStatusNative(@Param("status") String status, Pageable pageable);
}
