package com.alterna.store.store.catalog.service;

import com.alterna.store.store.catalog.dto.VariantCreateRequest;
import com.alterna.store.store.catalog.dto.VariantResponse;
import com.alterna.store.store.catalog.dto.VariantUpdateRequest;
import com.alterna.store.store.catalog.entity.ProductEntity;
import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.catalog.mapper.ProductMapper;
import com.alterna.store.store.catalog.repository.ProductRepository;
import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.shared.exception.ConflictException;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantService {

	private final VariantRepository variantRepository;
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final InventoryBalanceRepository inventoryBalanceRepository;

	@Transactional
	public VariantResponse create(VariantCreateRequest req) {
		ProductEntity product = productRepository.findById(req.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product", req.getProductId()));
		if (req.getSku() != null && !req.getSku().isBlank() && variantRepository.existsBySku(req.getSku())) {
			throw new ConflictException("Variant with sku " + req.getSku() + " already exists");
		}
		VariantEntity e = VariantEntity.builder()
				.product(product)
				.name(req.getName())
				.sku(req.getSku() != null && !req.getSku().isBlank() ? req.getSku() : null)
				.attributes(req.getAttributes())
				.build();
		e = variantRepository.save(e);
		inventoryBalanceRepository.save(InventoryBalanceEntity.builder().variant(e).quantity(0).reserved(0).build());
		return productMapper.toVariantResponse(e);
	}

	@Transactional(readOnly = true)
	public VariantResponse getById(Long id) {
		VariantEntity e = variantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Variant", id));
		return productMapper.toVariantResponse(e);
	}

	@Transactional(readOnly = true)
	public List<VariantResponse> findByProductId(Long productId) {
		return variantRepository.findByProductId(productId).stream()
				.map(productMapper::toVariantResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public VariantResponse update(Long id, VariantUpdateRequest req) {
		VariantEntity e = variantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Variant", id));
		if (req.getName() != null) e.setName(req.getName());
		if (req.getSku() != null) e.setSku(req.getSku().isBlank() ? null : req.getSku());
		if (req.getAttributes() != null) e.setAttributes(req.getAttributes());
		e = variantRepository.save(e);
		return productMapper.toVariantResponse(e);
	}

	@Transactional
	public void delete(Long id) {
		if (!variantRepository.existsById(id)) throw new ResourceNotFoundException("Variant", id);
		variantRepository.deleteById(id);
	}
}
