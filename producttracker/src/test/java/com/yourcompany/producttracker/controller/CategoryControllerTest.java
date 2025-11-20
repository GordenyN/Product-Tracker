package com.yourcompany.producttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.producttracker.dto.CategoryRequestDto;
import com.yourcompany.producttracker.dto.CategoryResponseDto;
import com.yourcompany.producttracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCategory() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", "All kinds of electronics");
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Electronics", "All kinds of electronics");

        when(categoryService.createCategory(any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("All kinds of electronics"));
    }

    @Test
    public void testGetCategoryById() throws Exception {
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Electronics", "All kinds of electronics");

        when(categoryService.getCategoryById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("All kinds of electronics"));
    }

    @Test
    public void testGetAllCategories() throws Exception {
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Electronics", "All kinds of electronics");

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].description").value("All kinds of electronics"));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", "All kinds of electronic devices");
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Electronics", "All kinds of electronic devices");

        when(categoryService.updateCategory(any(Long.class), any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("All kinds of electronic devices"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}