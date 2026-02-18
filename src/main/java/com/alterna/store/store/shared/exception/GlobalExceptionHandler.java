package com.alterna.store.store.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.builder()
				.message(ex.getMessage())
				.status(HttpStatus.NOT_FOUND.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.builder()
				.message(ex.getMessage())
				.status(HttpStatus.CONFLICT.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.builder()
				.message(ex.getMessage())
				.status(HttpStatus.BAD_REQUEST.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.errors(ex.getErrors())
				.build());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		List<ApiError.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid"))
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.builder()
				.message("Validation failed")
				.status(HttpStatus.BAD_REQUEST.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.errors(errors)
				.build());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.builder()
				.message("Invalid username or password")
				.status(HttpStatus.UNAUTHORIZED.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.builder()
				.message("Access denied")
				.status(HttpStatus.FORBIDDEN.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiError> handleDataAccess(DataAccessException ex, HttpServletRequest req) {
		log.warn("Data access error at {}: {}", req.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.builder()
				.message("Error temporal de base de datos. Por favor, intenta de nuevo en unos segundos.")
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.builder()
				.message(ex.getMessage() != null ? ex.getMessage() : "Internal server error")
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.path(req.getRequestURI())
				.timestamp(Instant.now())
				.build());
	}
}
