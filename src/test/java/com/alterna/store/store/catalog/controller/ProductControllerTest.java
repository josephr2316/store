package com.alterna.store.store.catalog.controller;

import com.alterna.store.store.catalog.dto.ProductResponse;
import com.alterna.store.store.catalog.service.ProductService;
import com.alterna.store.store.security.jwt.JwtService;
import com.alterna.store.store.security.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductService productService;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private UserService userService;

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void listProducts_devuelveLista() throws Exception {
		when(productService.findAll(true)).thenReturn(List.of(
				ProductResponse.builder().id(1L).name("Producto 1").sku("SKU-1").build()
		));

		mockMvc.perform(get("/products").param("includeVariants", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value("Producto 1"))
				.andExpect(jsonPath("$[0].sku").value("SKU-1"));
	}

}
