package com.alterna.store.store.inventory.entity;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.inventory.enums.AdjustmentReason;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "inventory_adjustment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id", nullable = false)
	private VariantEntity variant;

	@Column(name = "quantity_delta", nullable = false)
	private Integer quantityDelta;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 100)
	private AdjustmentReason reason;

	@Column(columnDefinition = "TEXT")
	private String note;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;
}
