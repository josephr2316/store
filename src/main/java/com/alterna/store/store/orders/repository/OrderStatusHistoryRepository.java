package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {

	@Query("SELECT h FROM OrderStatusHistoryEntity h WHERE h.order.id = :orderId ORDER BY h.changedAt DESC")
	List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtDesc(@Param("orderId") Long orderId);
}
