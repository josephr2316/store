package com.alterna.store.store.security.auth.service;

import com.alterna.store.store.security.auth.dto.LoginRequest;
import com.alterna.store.store.security.auth.dto.LoginResponse;
import com.alterna.store.store.security.jwt.JwtService;
import com.alterna.store.store.security.user.entity.UserEntity;
import com.alterna.store.store.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public LoginResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		UserEntity user = (UserEntity) userService.loadUserByUsername(request.getUsername());
		String token = jwtService.generateToken(user.getUsername());
		return LoginResponse.builder()
				.token(token)
				.username(user.getUsername())
				.role(user.getRole())
				.build();
	}
}
