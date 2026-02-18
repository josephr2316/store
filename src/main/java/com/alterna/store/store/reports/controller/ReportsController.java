package com.alterna.store.store.reports.controller;

import com.alterna.store.store.reports.dto.PagedResponse;
import com.alterna.store.store.reports.dto.SalesInRangeResponse;
import com.alterna.store.store.reports.dto.TopProductsResponse;
import com.alterna.store.store.reports.dto.WeeklySalesResponse;
import com.alterna.store.store.reports.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportsController {

	private final ReportsService reportsService;

	@GetMapping("/weekly-sales")
	@Operation(summary = "Sales for a given week (delivered orders)")
	public ResponseEntity<WeeklySalesResponse> weeklySales(
			@RequestParam(name = "week", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
		LocalDate week = weekStart != null ? weekStart : LocalDate.now();
		return ResponseEntity.ok(reportsService.weeklySales(week));
	}

	@GetMapping("/sales-in-range")
	@Operation(summary = "Sales aggregated by week in a date range (from = 1 year back, to = selected date)")
	public ResponseEntity<SalesInRangeResponse> salesInRange(
			@RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(reportsService.salesInRange(from, to));
	}

	@GetMapping("/top-products")
	@Operation(summary = "Top products by quantity sold (paginated)")
	public ResponseEntity<PagedResponse<TopProductsResponse>> topProducts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(name = "from", required = false) Instant from,
			@RequestParam(name = "to", required = false) Instant to) {
		return ResponseEntity.ok(reportsService.topProductsPaginated(from, to, page, size));
	}
}
