package com.alterna.store.store.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WeeklySalesResponse {
	private LocalDate weekStart;
	private Long orderCount;
	private BigDecimal totalAmount;
	/** Sales per day (always 7 entries: Mon–Sun for the week) */
	private List<DailySaleDto> dailyBreakdown;
}
