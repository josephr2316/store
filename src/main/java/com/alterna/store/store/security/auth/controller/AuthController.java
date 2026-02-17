package com.alterna.store.store.security.auth.controller;

import com.alterna.store.store.security.auth.dto.LoginRequest;
import com.alterna.store.store.security.auth.dto.LoginResponse;
import com.alterna.store.store.security.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Login y JWT")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "Login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
