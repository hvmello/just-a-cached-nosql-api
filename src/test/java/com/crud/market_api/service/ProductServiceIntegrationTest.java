package com.crud.market_api.service;

import com.crud.market_api.model.dto.ProductDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceIntegrationTest.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    private static ProductDto testProduct;

    @BeforeEach
    void setUp() {
        // Limpa o cache antes de cada teste
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    @Order(1)
    @DisplayName("Should create product and verify cache behavior")
    void shouldCreateAndCacheProduct() {
        // Arrange
        ProductDto newProduct = new ProductDto();
        newProduct.setName("Test Redis Cache");
        newProduct.setType("Electronics");

        // Act
        testProduct = productService.createProduct(newProduct);
        logger.info("Created product with ID: {}", testProduct.getId());

        // Assert
        assertThat(testProduct.getId()).isNotNull();
        assertThat(testProduct.getName()).isEqualTo("Test Redis Cache");
    }

    @Test
    @Order(2)
    @DisplayName("Should retrieve product from cache on second call")
    void shouldUseCache() {
        // First call - should hit database
        long start1 = System.nanoTime();
        ProductDto firstCall = productService.findById(testProduct.getId());
        long duration1 = System.nanoTime() - start1;
        logger.info("First call duration: {} ns", duration1);

        // Second call - should hit cache
        long start2 = System.nanoTime();
        ProductDto secondCall = productService.findById(testProduct.getId());
        long duration2 = System.nanoTime() - start2;
        logger.info("Second call duration: {} ns", duration2);

        // Assertions
        assertThat(secondCall).isNotNull();
        assertThat(duration2).isLessThan(duration1);

        // Verify cache content
        Object cachedValue = cacheManager.getCache("products")
                .get(testProduct.getId())
                .get();
        assertThat(cachedValue).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("Should update cache when product is updated")
    void shouldUpdateCache() {
        // Update product
        testProduct.setName("Updated Name");
        ProductDto updatedProduct = productService.updateProduct(testProduct);

        // Get from cache
        Object cachedValue = cacheManager.getCache("products")
                .get(testProduct.getId())
                .get();

        // Assert
        assertThat(cachedValue).isInstanceOf(ProductDto.class);
        ProductDto cachedProduct = (ProductDto) cachedValue;
        assertThat(cachedProduct.getName()).isEqualTo("Updated Name");
    }

    @Test
    @Order(4)
    @DisplayName("Should evict cache when product is deleted")
    void shouldEvictCache() {
        // Act
        productService.deleteProduct(testProduct.getId());

        // Assert
        Object cachedValue = cacheManager.getCache("products")
                .get(testProduct.getId());
        assertThat(cachedValue).isNull();
    }

    @Test
    @Order(5)
    @DisplayName("Should cache list of all products")
    void shouldCacheAllProducts() {
        // Create multiple products
        ProductDto product1 = new ProductDto();
        product1.setName("Product 1");
        product1.setType("Type 1");
        productService.createProduct(product1);

        ProductDto product2 = new ProductDto();
        product2.setName("Product 2");
        product2.setType("Type 2");
        productService.createProduct(product2);

        // First call - should hit database
        long start1 = System.nanoTime();
        var firstCall = productService.getAllProducts();
        long duration1 = System.nanoTime() - start1;
        logger.info("GetAll first call duration: {} ns", duration1);

        // Second call - should hit cache
        long start2 = System.nanoTime();
        var secondCall = productService.getAllProducts();
        long duration2 = System.nanoTime() - start2;
        logger.info("GetAll second call duration: {} ns", duration2);

        // Assertions
        assertThat(secondCall).hasSameSizeAs(firstCall);
        assertThat(duration2).isLessThan(duration1);
        assertThat(cacheManager.getCache("allProducts")).isNotNull();
    }
}