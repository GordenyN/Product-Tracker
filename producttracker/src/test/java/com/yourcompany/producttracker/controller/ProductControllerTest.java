package com.yourcompany.producttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.producttracker.dto.ProductRequestDto;
import com.yourcompany.producttracker.dto.ProductResponseDto;
import com.yourcompany.producttracker.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateProduct() throws Exception {
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setNameEn("Laptop");
        requestDto.setNameRu("Ноутбук");
        requestDto.setCharacteristics("High-end gaming laptop");
        requestDto.setWeight(2.5);
        requestDto.setSize("15 inch");
        requestDto.setExpiryDate(LocalDate.now().plusYears(1));
        requestDto.setStockQuantity(10);
        requestDto.setCategoryId(1L);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setNameEn("Laptop");
        responseDto.setNameRu("Ноутбук");
        responseDto.setCharacteristics("High-end gaming laptop");
        responseDto.setWeight(2.5);
        responseDto.setSize("15 inch");
        responseDto.setExpiryDate(LocalDate.now().plusYears(1));
        responseDto.setStockQuantity(10);
        responseDto.setCategoryId(1L);
        responseDto.setCategoryName("Electronics");

        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nameEn").value("Laptop"));
    }

    @Test
    public void testGetProductById() throws Exception {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setNameEn("Laptop");
        responseDto.setNameRu("Ноутбук");
        responseDto.setCharacteristics("High-end gaming laptop");
        responseDto.setWeight(2.5);
        responseDto.setSize("15 inch");
        responseDto.setExpiryDate(LocalDate.now().plusYears(1));
        responseDto.setStockQuantity(10);
        responseDto.setCategoryId(1L);
        responseDto.setCategoryName("Electronics");

        when(productService.getProductById(1L)).thenReturn(Optional.of(responseDto));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nameEn").value("Laptop"));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setNameEn("Laptop");
        responseDto.setNameRu("Ноутбук");
        responseDto.setCharacteristics("High-end gaming laptop");
        responseDto.setWeight(2.5);
        responseDto.setSize("15 inch");
        responseDto.setExpiryDate(LocalDate.now().plusYears(1));
        responseDto.setStockQuantity(10);
        responseDto.setCategoryId(1L);
        responseDto.setCategoryName("Electronics");

        when(productService.getAllProducts()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nameEn").value("Laptop"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setNameEn("Laptop");
        requestDto.setNameRu("Ноутбук");
        requestDto.setCharacteristics("Updated gaming laptop");
        requestDto.setWeight(2.5);
        requestDto.setSize("15 inch");
        requestDto.setExpiryDate(LocalDate.now().plusYears(1));
        requestDto.setStockQuantity(5);
        requestDto.setCategoryId(1L);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setNameEn("Laptop");
        responseDto.setNameRu("Ноутбук");
        responseDto.setCharacteristics("Updated gaming laptop");
        responseDto.setWeight(2.5);
        responseDto.setSize("15 inch");
        responseDto.setExpiryDate(LocalDate.now().plusYears(1));
        responseDto.setStockQuantity(5);
        responseDto.setCategoryId(1L);
        responseDto.setCategoryName("Electronics");

        when(productService.updateProduct(any(Long.class), any(ProductRequestDto.class))).thenReturn(Optional.of(responseDto));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nameEn").value("Laptop"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProductsByCategoryId() throws Exception {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(1L);
        responseDto.setNameEn("Laptop");
        responseDto.setNameRu("Ноутбук");
        responseDto.setCharacteristics("High-end gaming laptop");
        responseDto.setWeight(2.5);
        responseDto.setSize("15 inch");
        responseDto.setExpiryDate(LocalDate.now().plusYears(1));
        responseDto.setStockQuantity(10);
        responseDto.setCategoryId(1L);
        responseDto.setCategoryName("Electronics");

        when(productService.getProductsByCategoryId(1L)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nameEn").value("Laptop"));
    }
}
