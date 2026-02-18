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
	/** Sales per week in the range (one entry per week) */
	private List<PeriodSaleDto> byWeek;
	/** Sales per day in the range (one entry per day) */
	private List<DailySaleDto> byDay;
}
