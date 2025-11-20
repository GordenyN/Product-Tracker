package com.yourcompany.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private Long id;
    private String nameEn;
    private String nameRu;
    private String characteristics;
    private Double weight;
    private String size;
    private LocalDate expiryDate;
    private Integer stockQuantity;
    private Category category;
    private Long categoryId;
    private String categoryName;
}

