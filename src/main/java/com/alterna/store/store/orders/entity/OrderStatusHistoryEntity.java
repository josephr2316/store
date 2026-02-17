package com.alterna.store.store.orders.entity;

import com.alterna.store.store.orders.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "order_status_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@Enumerated(EnumType.STRING)
	@Column(name = "from_status")
	private OrderStatus fromStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_status", nullable = false)
	private OrderStatus toStatus;

	@Column(columnDefinition = "TEXT")
	private String reason;

	@CreatedBy
	@Column(name = "changed_by")
	private String changedBy;

	@CreatedDate
	@Column(name = "changed_at", nullable = false, updatable = false)
	private Instant changedAt;
}
