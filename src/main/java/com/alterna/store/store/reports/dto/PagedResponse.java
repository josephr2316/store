package com.alterna.store.store.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
	private List<T> content;
	private long totalElements;
	private int totalPages;
	private int number;  // 0-based page index
	private int size;
}
