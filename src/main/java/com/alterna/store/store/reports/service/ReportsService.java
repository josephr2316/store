package com.alterna.store.store.reports.service;

import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.reports.dto.TopProductsResponse;
import com.alterna.store.store.reports.dto.WeeklySalesResponse;
import com.alterna.store.store.reports.repository.ReportsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {

	private static final Logger log = LoggerFactory.getLogger(ReportsService.class);

	private final ReportsRepository reportsRepository;
	private final VariantRepository variantRepository;

	private static BigDecimal toBigDecimal(Object o) {
		if (o == null) return BigDecimal.ZERO;
		if (o instanceof BigDecimal bd) return bd;
		if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
		try {
			return new BigDecimal(o.toString());
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Transactional(readOnly = true)
	public WeeklySalesResponse weeklySales(LocalDate weekStart) {
		try {
			Instant from = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
			Instant to = from.plus(7, ChronoUnit.DAYS);
			Object[] row = reportsRepository.deliveredOrdersCountAndTotal(from, to, OrderStatus.DELIVERED);
			Long count = row != null && row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : 0L;
			BigDecimal total = row != null && row.length > 1 ? toBigDecimal(row[1]) : BigDecimal.ZERO;
			return WeeklySalesResponse.builder()
					.weekStart(weekStart)
					.orderCount(count)
					.totalAmount(total)
					.build();
		} catch (Exception e) {
			log.warn("Reports: weeklySales failed, returning empty: {}", e.getMessage());
			return WeeklySalesResponse.builder()
					.weekStart(weekStart)
					.orderCount(0L)
					.totalAmount(BigDecimal.ZERO)
					.build();
		}
	}

	@Transactional(readOnly = true)
	public List<TopProductsResponse> topProducts(int limit, Instant from, Instant to) {
		try {
			if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
			if (to == null) to = Instant.now();
			List<Object[]> rows = reportsRepository.topVariantsByQuantity(from, to, OrderStatus.DELIVERED, PageRequest.of(0, limit));
			if (rows == null) return Collections.emptyList();
			return rows.stream().map(row -> {
				Long variantId = row != null && row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : null;
				Long qty = row != null && row.length > 1 && row[1] != null ? ((Number) row[1]).longValue() : 0L;
				BigDecimal revenue = row != null && row.length > 2 ? toBigDecimal(row[2]) : BigDecimal.ZERO;
				String sku = variantId != null ? variantRepository.findById(variantId).map(v -> v.getSku()).orElse("") : "";
				return TopProductsResponse.builder()
						.variantId(variantId)
						.variantSku(sku != null ? sku : "")
						.quantitySold(qty)
						.revenue(revenue)
						.build();
			}).collect(Collectors.toList());
		} catch (Exception e) {
			log.warn("Reports: topProducts failed, returning empty: {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
