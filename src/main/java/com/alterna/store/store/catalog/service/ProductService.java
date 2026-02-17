package com.alterna.store.store.catalog.service;

import com.alterna.store.store.catalog.dto.ProductCreateRequest;
import com.alterna.store.store.catalog.dto.ProductResponse;
import com.alterna.store.store.catalog.dto.ProductUpdateRequest;
import com.alterna.store.store.catalog.entity.ProductEntity;
import com.alterna.store.store.catalog.mapper.ProductMapper;
import com.alterna.store.store.catalog.repository.ProductRepository;
import com.alterna.store.store.shared.exception.ConflictException;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	@Transactional
	public ProductResponse create(ProductCreateRequest req) {
		if (req.getSku() != null && !req.getSku().isBlank() && productRepository.existsBySku(req.getSku())) {
			throw new ConflictException("Product with sku " + req.getSku() + " already exists");
		}
		ProductEntity e = ProductEntity.builder()
				.name(req.getName())
				.description(req.getDescription())
				.sku(req.getSku() != null && !req.getSku().isBlank() ? req.getSku() : null)
				.build();
		e = productRepository.save(e);
		return productMapper.toResponse(e, false);
	}

	@Transactional(readOnly = true)
	public ProductResponse getById(Long id, boolean includeVariants) {
		ProductEntity e = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product", id));
		return productMapper.toResponse(e, includeVariants);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> findAll(boolean includeVariants) {
		return productRepository.findAll().stream()
				.map(p -> productMapper.toResponse(p, includeVariants))
				.collect(Collectors.toList());
	}

	@Transactional
	public ProductResponse update(Long id, ProductUpdateRequest req) {
		ProductEntity e = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product", id));
		if (req.getName() != null) e.setName(req.getName());
		if (req.getDescription() != null) e.setDescription(req.getDescription());
		if (req.getSku() != null) {
			if (!req.getSku().isBlank() && productRepository.existsBySku(req.getSku()) && !req.getSku().equals(e.getSku())) {
				throw new ConflictException("Product with sku " + req.getSku() + " already exists");
			}
			e.setSku(req.getSku().isBlank() ? null : req.getSku());
		}
		e = productRepository.save(e);
		return productMapper.toResponse(e, false);
	}

	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsById(id)) throw new ResourceNotFoundException("Product", id);
		productRepository.deleteById(id);
	}
}
