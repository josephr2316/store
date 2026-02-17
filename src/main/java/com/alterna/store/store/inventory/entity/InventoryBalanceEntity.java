package com.alterna.store.store.inventory.entity;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBalanceEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id", nullable = false, unique = true)
	private VariantEntity variant;

	@Column(nullable = false)
	@Builder.Default
	private Integer quantity = 0;

	@Column(nullable = false)
	@Builder.Default
	private Integer reserved = 0;
}
