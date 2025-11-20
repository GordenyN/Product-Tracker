package com.yourcompany.producttracker.dto;

import lombok.Data;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
public class ProductResponseDto {

    private Long id;
    private String nameEn;
    private String nameRu;
    private String characteristics;
    private Double weight;
    private String size;
    private LocalDate expiryDate;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
}
