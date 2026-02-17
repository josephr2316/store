package com.alterna.store.store.reports.controller;

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
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportsController {

	private final ReportsService reportsService;

	@GetMapping("/weekly-sales")
	@Operation(summary = "Sales for a given week (delivered orders)")
	public ResponseEntity<WeeklySalesResponse> weeklySales(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
		return ResponseEntity.ok(reportsService.weeklySales(weekStart));
	}

	@GetMapping("/top-products")
	@Operation(summary = "Top products by quantity sold")
	public ResponseEntity<List<TopProductsResponse>> topProducts(
			@RequestParam(defaultValue = "10") int limit,
			@RequestParam(required = false) Instant from,
			@RequestParam(required = false) Instant to) {
		return ResponseEntity.ok(reportsService.topProducts(limit, from, to));
	}
}
