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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

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
    @Override
    public ProductDto findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);
        return productDto;
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