package com.alterna.store.store.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SalesInRangeResponse {
	private LocalDate from;
	private LocalDate to;
	private BigDecimal totalAmount;
	private Long totalOrders;
	/** Ventas por semana en el rango (una entrada por semana) */
	private List<PeriodSaleDto> byWeek;
}
