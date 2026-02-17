package com.alterna.store.store.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {
	String message;
	int status;
	String path;
	Instant timestamp;
	List<FieldError> errors;

	@Value
	public static class FieldError {
		String field;
		String message;
	}
}
