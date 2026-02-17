package com.alterna.store.store.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WeeklySalesResponse {
	private LocalDate weekStart;
	private Long orderCount;
	private BigDecimal totalAmount;
}
