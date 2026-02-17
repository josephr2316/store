package com.alterna.store.store.security.auth.controller;

import com.alterna.store.store.security.auth.dto.LoginRequest;
import com.alterna.store.store.security.auth.service.AuthService;
import com.alterna.store.store.security.jwt.JwtService;
import com.alterna.store.store.security.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private UserDetailsService userDetailsService;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private UserService userService;

	@Test
	void login_conCredencialesCorrectas_devuelveToken() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setUsername("admin");
		request.setPassword("admin123");

		when(authService.login(any(LoginRequest.class)))
				.thenReturn(com.alterna.store.store.security.auth.dto.LoginResponse.builder()
						.token("fake-jwt-token")
						.username("admin")
						.role("ROLE_ADMIN")
						.build());

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("fake-jwt-token"))
				.andExpect(jsonPath("$.username").value("admin"))
				.andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
	}

	@Test
	void login_sinBody_devuelve400() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}
}
