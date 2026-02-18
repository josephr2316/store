package com.alterna.store.store.reports.service;

import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.reports.dto.DailySaleDto;
import com.alterna.store.store.reports.dto.PagedResponse;
import com.alterna.store.store.reports.dto.PeriodSaleDto;
import com.alterna.store.store.reports.dto.SalesInRangeResponse;
import com.alterna.store.store.reports.dto.TopProductsResponse;
import com.alterna.store.store.reports.dto.WeeklySalesResponse;
import com.alterna.store.store.reports.repository.ReportsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	private static LocalDate toLocalDate(Object o) {
		if (o == null) return null;
		if (o instanceof LocalDate ld) return ld;
		if (o instanceof java.sql.Date d) return d.toLocalDate();
		if (o instanceof java.util.Date d) return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return null;
	}

	@Transactional(readOnly = true)
	public WeeklySalesResponse weeklySales(LocalDate weekStart) {
		try {
			Instant from = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
			Instant to = from.plus(7, ChronoUnit.DAYS);
			Object[] row = reportsRepository.deliveredOrdersCountAndTotal(from, to, OrderStatus.DELIVERED);
			Long count = row != null && row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : 0L;
			BigDecimal total = row != null && row.length > 1 ? toBigDecimal(row[1]) : BigDecimal.ZERO;

			Map<LocalDate, DailySaleDto> byDay = new LinkedHashMap<>();
			List<Object[]> rows = reportsRepository.deliveredOrdersCountAndTotalByDay(from, to, OrderStatus.DELIVERED);
			if (rows != null) {
				for (Object[] r : rows) {
					LocalDate d = toLocalDate(r[0]);
					if (d == null) continue;
					Long c = r.length > 1 && r[1] != null ? ((Number) r[1]).longValue() : 0L;
					BigDecimal amt = r.length > 2 ? toBigDecimal(r[2]) : BigDecimal.ZERO;
					byDay.put(d, DailySaleDto.builder().date(d).orderCount(c).totalAmount(amt).build());
				}
			}
			List<DailySaleDto> dailyBreakdown = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				LocalDate d = weekStart.plusDays(i);
				dailyBreakdown.add(byDay.getOrDefault(d, DailySaleDto.builder().date(d).orderCount(0L).totalAmount(BigDecimal.ZERO).build()));
			}

			return WeeklySalesResponse.builder()
					.weekStart(weekStart)
					.orderCount(count)
					.totalAmount(total)
					.dailyBreakdown(dailyBreakdown)
					.build();
		} catch (Exception e) {
			log.warn("Reports: weeklySales failed, returning empty: {}", e.getMessage());
			List<DailySaleDto> emptyDaily = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				emptyDaily.add(DailySaleDto.builder().date(weekStart.plusDays(i)).orderCount(0L).totalAmount(BigDecimal.ZERO).build());
			}
			return WeeklySalesResponse.builder()
					.weekStart(weekStart)
					.orderCount(0L)
					.totalAmount(BigDecimal.ZERO)
					.dailyBreakdown(emptyDaily)
					.build();
		}
	}

	@Transactional(readOnly = true)
	public SalesInRangeResponse salesInRange(LocalDate from, LocalDate to) {
		if (from == null) from = LocalDate.now().minusYears(1);
		if (to == null) to = LocalDate.now();
		if (!from.isBefore(to) && !from.equals(to)) {
			LocalDate tmp = from; from = to; to = tmp;
		}
		List<PeriodSaleDto> byWeek = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;
		long totalOrders = 0L;
		try {
			LocalDate weekStart = from;
			while (!weekStart.isAfter(to)) {
				Instant instFrom = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
				Instant instTo = instFrom.plus(7, ChronoUnit.DAYS);
				Object[] row = reportsRepository.deliveredOrdersCountAndTotal(instFrom, instTo, OrderStatus.DELIVERED);
				Long count = row != null && row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : 0L;
				BigDecimal amt = row != null && row.length > 1 ? toBigDecimal(row[1]) : BigDecimal.ZERO;
				byWeek.add(PeriodSaleDto.builder().periodStart(weekStart).orderCount(count).totalAmount(amt).build());
				totalAmount = totalAmount.add(amt);
				totalOrders += count;
				weekStart = weekStart.plusWeeks(1);
			}
			return SalesInRangeResponse.builder()
					.from(from)
					.to(to)
					.totalAmount(totalAmount)
					.totalOrders(totalOrders)
					.byWeek(byWeek)
					.build();
		} catch (Exception e) {
			log.warn("Reports: salesInRange failed: {}", e.getMessage());
			return SalesInRangeResponse.builder()
					.from(from)
					.to(to)
					.totalAmount(BigDecimal.ZERO)
					.totalOrders(0L)
					.byWeek(Collections.emptyList())
					.build();
		}
	}

	@Transactional(readOnly = true)
	public PagedResponse<TopProductsResponse> topProductsPaginated(Instant from, Instant to, int page, int size) {
		try {
			if (from == null) from = Instant.now().minus(365, ChronoUnit.DAYS);
			if (to == null) to = Instant.now();
			if (size <= 0) size = 10;
			if (page < 0) page = 0;
			long totalElements = reportsRepository.countDistinctVariantsSold(from, to, OrderStatus.DELIVERED);
			int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
			Pageable pageable = PageRequest.of(page, size);
			List<Object[]> rows = reportsRepository.topVariantsByQuantity(from, to, OrderStatus.DELIVERED, pageable);
			List<TopProductsResponse> content = rows == null ? Collections.emptyList() : rows.stream().map(row -> {
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
			return PagedResponse.<TopProductsResponse>builder()
					.content(content)
					.totalElements(totalElements)
					.totalPages(totalPages)
					.number(page)
					.size(size)
					.build();
		} catch (Exception e) {
			log.warn("Reports: topProductsPaginated failed: {}", e.getMessage());
			return PagedResponse.<TopProductsResponse>builder()
					.content(Collections.emptyList())
					.totalElements(0L)
					.totalPages(0)
					.number(0)
					.size(size)
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
