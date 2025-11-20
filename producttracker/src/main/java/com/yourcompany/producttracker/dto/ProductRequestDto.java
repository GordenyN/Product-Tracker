package com.yourcompany.producttracker.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
public class ProductRequestDto {

    @NotBlank(message = "Наименование на английском не может быть пустым")
    private String nameEn;

    @NotBlank(message = "Наименование на русском не может быть пустым")
    private String nameRu;

    private String characteristics;

    private Double weight;

    private String size;

    private LocalDate expiryDate;

    @NotNull(message = "Количество на складе не может быть null")
    @Min(value = 0, message = "Количество на складе не может быть отрицательным")
    private Integer stockQuantity;

    private Long categoryId;
}
