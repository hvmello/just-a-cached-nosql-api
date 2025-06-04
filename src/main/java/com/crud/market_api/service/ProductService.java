package com.crud.market_api.service;

import com.crud.market_api.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(ProductDto productDto);
    ProductDto findById(Long id);
    List<ProductDto> getAllProducts();
    void deleteProduct(Long id);

}
