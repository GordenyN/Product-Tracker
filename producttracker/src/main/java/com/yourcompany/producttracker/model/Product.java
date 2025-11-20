package com.yourcompany.producttracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "products") // Явно указываем имя таблицы
@Data // Lombok: автоматически генерирует геттеры, сеттеры, toString, equals и hashCode методы.
@NoArgsConstructor // Lombok: генерирует конструктор без аргументов, необходимый для JPA.
@AllArgsConstructor // Lombok: генерирует конструктор со всеми аргументами.
public class Product {

    @Id // Указывает, что это первичный ключ.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID (автоинкремент).
    private Long id;

    /**
     * Наименование продукта на английском языке.
     * Поле не может быть null.
     */
    @Column(nullable = false)
    private String nameEn;

    /**
     * Наименование продукта на русском языке.
     * Поле не может быть null.
     */
    @Column(nullable = false)
    private String nameRu;

    /**
     * Подробные характеристики продукта в виде строки (например: "Вес: 1кг, Размер: 20x30x10см").
     * Хранится как TEXT в базе данных.
     */
    @Column(columnDefinition = "TEXT")
    private String characteristics;

    /**
     * Вес продукта.
     */
    private Double weight;

    /**
     * Размер продукта.
     */
    private String size;

    /**
     * Срок годности продукта.
     * Используется LocalDate для хранения только даты без времени.
     */
    private LocalDate expiryDate;

    /**
     * Количество продукта на складе.
     * Поле не может быть null.
     */
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
}