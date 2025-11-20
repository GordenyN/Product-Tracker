package com.yourcompany.notificationservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для десериализации данных о продукте из Kafka.
 * Поля должны соответствовать полям сущности Product в product-service.
 */
@Data
public class Product {
    private Long id;
    private String nameEn;
    private String nameRu;
    private String description;
    private String characteristics;
    private BigDecimal weight;
    private String size;
    private String material;
    private String brand;
    private String manufacturer;
    private String countryOfOrigin;
    private LocalDate expiryDate;
    private Integer stockQuantity;
    private BigDecimal price;
}
