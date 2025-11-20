package com.yourcompany.producttracker.service;

import com.yourcompany.producttracker.dto.ProductRequestDto;
import com.yourcompany.producttracker.dto.ProductResponseDto;
import com.yourcompany.producttracker.model.Category;
import com.yourcompany.producttracker.model.Product;
import com.yourcompany.producttracker.repository.CategoryRepository;
import com.yourcompany.producttracker.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException; // Add this import
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper; // Add mock for ObjectMapper

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate; // Add mock for KafkaTemplate

    @InjectMocks
    private ProductService productService;

    @Test
    public void testCreateProductWithCategory() throws JsonProcessingException {
        Category category = new Category(1L, "Electronics", "All kinds of electronics");
        Product product = new Product();
        product.setId(1L);
        product.setNameEn("Laptop");
        product.setNameRu("Ноутбук");
        product.setCategory(category);

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setNameEn("Laptop");
        requestDto.setNameRu("Ноутбук");
        requestDto.setStockQuantity(20); // Set a default stock quantity
        requestDto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        // Mock ObjectMapper behavior
        when(objectMapper.writeValueAsString(any(ProductResponseDto.class))).thenReturn("{\"id\":1,\"nameEn\":\"Laptop\"}");
        // Mock KafkaTemplate behavior
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        ProductResponseDto responseDto = productService.createProduct(requestDto);

        assertEquals(1L, responseDto.getId());
        assertEquals("Laptop", responseDto.getNameEn());
        assertEquals(1L, responseDto.getCategoryId());
        assertEquals("Electronics", responseDto.getCategoryName());
    }
}