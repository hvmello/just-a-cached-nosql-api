package com.crud.market_api.service.impl;

import com.crud.market_api.exception.ResourceNotFoundException;
import com.crud.market_api.model.dto.ProductDto;
import com.crud.market_api.model.entity.Product;
import com.crud.market_api.repository.ProductRepository;
import com.crud.market_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @CachePut(value = "products", key = "#result.id")
    @CacheEvict(value = "allProducts", allEntries = true)
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        Product savedProduct = productRepository.save(product);
        BeanUtils.copyProperties(savedProduct, productDto);
        return productDto;
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDto findById(String id) {
        logger.info("Finding product with ID: {}", id);
        long startTime = System.nanoTime(); // Using nanoTime for more precise measurements

        try {
            return productRepository.findById(id)
                    .map(product -> {
                        ProductDto dto = new ProductDto();
                        BeanUtils.copyProperties(product, dto);
                        return dto;
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        } finally {
            long duration = System.nanoTime() - startTime;
            logger.info("Product find operation took: {} ms", duration / 1_000_000.0); // Convert to milliseconds
        }
    }


    @Cacheable(value = "allProducts")
    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> {
                    ProductDto dto = new ProductDto();
                    BeanUtils.copyProperties(product, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Caching(
            put = {@CachePut(value = "products", key = "#productDto.id")},
            evict = {@CacheEvict(value = "allProducts", allEntries = true)}
    )
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product existingProduct = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productDto.getId()));

        BeanUtils.copyProperties(productDto, existingProduct);
        existingProduct.setId(productDto.getId());

        Product updatedProduct = productRepository.save(existingProduct);
        BeanUtils.copyProperties(updatedProduct, productDto);
        return productDto;
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "products", key = "#id"),
                    @CacheEvict(value = "allProducts", allEntries = true)
            }
    )
    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}