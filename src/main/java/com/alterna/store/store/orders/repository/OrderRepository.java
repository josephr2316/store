package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByStatus(OrderStatus status);

	/** Load all orders with items and variant (avoids LazyInitializationException when mapping to DTO). */
	@Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.variant ORDER BY o.createdAt DESC")
	List<OrderEntity> findAllWithItems();

	/** Load orders by status with items and variant. */
	@Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.variant WHERE o.status = :status ORDER BY o.createdAt DESC")
	List<OrderEntity> findByStatusWithItems(@Param("status") OrderStatus status);

	List<OrderEntity> findByCreatedAtBetween(Instant from, Instant to);
}
