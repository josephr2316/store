package com.alterna.store.store.reports.service;

import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.reports.dto.TopProductsResponse;
import com.alterna.store.store.reports.dto.WeeklySalesResponse;
import com.alterna.store.store.reports.repository.ReportsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {

	private final ReportsRepository reportsRepository;
	private final VariantRepository variantRepository;

	@Transactional(readOnly = true)
	public WeeklySalesResponse weeklySales(LocalDate weekStart) {
		Instant from = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant to = from.plus(7, ChronoUnit.DAYS);
		Object[] row = reportsRepository.deliveredOrdersCountAndTotal(from, to);
		Long count = row[0] != null ? ((Number) row[0]).longValue() : 0L;
		BigDecimal total = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
		return WeeklySalesResponse.builder()
				.weekStart(weekStart)
				.orderCount(count)
				.totalAmount(total)
				.build();
	}

	@Transactional(readOnly = true)
	public List<TopProductsResponse> topProducts(int limit, Instant from, Instant to) {
		if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
		if (to == null) to = Instant.now();
		List<Object[]> rows = reportsRepository.topVariantsByQuantity(from, to, PageRequest.of(0, limit));
		return rows.stream().map(row -> {
			Long variantId = (Long) row[0];
			Long qty = ((Number) row[1]).longValue();
			BigDecimal revenue = (BigDecimal) row[2];
			String sku = variantRepository.findById(variantId).map(v -> v.getSku()).orElse("");
			return TopProductsResponse.builder()
					.variantId(variantId)
					.variantSku(sku)
					.quantitySold(qty)
					.revenue(revenue != null ? revenue : BigDecimal.ZERO)
					.build();
		}).collect(Collectors.toList());
	}
}
