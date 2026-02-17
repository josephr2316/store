package com.alterna.store.store.shared.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
	private final List<ApiError.FieldError> errors;
	public ValidationException(String message) {
		super(message);
		this.errors = List.of();
	}
	public ValidationException(String message, List<ApiError.FieldError> errors) {
		super(message);
		this.errors = errors != null ? errors : List.of();
	}
}
