package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
	List<OrderItemEntity> findByOrderId(Long orderId);

	/** Load items with variant fetched (avoids LazyInitializationException). Batch by order ids. */
	@Query("SELECT i FROM OrderItemEntity i JOIN FETCH i.variant WHERE i.order.id IN :orderIds")
	List<OrderItemEntity> findByOrderIdInWithVariant(@Param("orderIds") List<Long> orderIds);
}
