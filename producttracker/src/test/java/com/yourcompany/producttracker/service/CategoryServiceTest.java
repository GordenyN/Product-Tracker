package com.yourcompany.producttracker.service;

import com.yourcompany.producttracker.dto.CategoryRequestDto;
import com.yourcompany.producttracker.dto.CategoryResponseDto;
import com.yourcompany.producttracker.model.Category;
import com.yourcompany.producttracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testCreateCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", "All kinds of electronics");
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("All kinds of electronics");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDto responseDto = categoryService.createCategory(requestDto);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals("Electronics", responseDto.getName());
    }

    @Test
    public void testGetCategoryById() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("All kinds of electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponseDto responseDto = categoryService.getCategoryById(1L);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
    }

    @Test
    public void testGetAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("All kinds of electronics");

        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        List<CategoryResponseDto> responseDtos = categoryService.getAllCategories();

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals(1L, responseDtos.get(0).getId());
    }

    @Test
    public void testUpdateCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", "All kinds of electronic devices");
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("All kinds of electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDto responseDto = categoryService.updateCategory(1L, requestDto);

        assertNotNull(responseDto);
        assertEquals("All kinds of electronic devices", responseDto.getDescription());
    }

    @Test
    public void testDeleteCategory() {
        categoryService.deleteCategory(1L);
    }
}
