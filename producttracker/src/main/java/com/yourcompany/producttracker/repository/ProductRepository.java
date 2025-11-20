package com.yourcompany.producttracker.repository;

import com.yourcompany.producttracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Product.
 * Наследуясь от JpaRepository, мы бесплатно получаем полный набор CRUD-операций.
 * Spring Data JPA сам реализует методы: save(), findById(), findAll(), deleteById() и т.д.
 * Также можно добавлять свои кастомные методы.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Кастомный метод для поиска продуктов, количество которых меньше заданного значения.
     * Spring Data JPA автоматически сгенерирует SQL-запрос по названию метода.
     * Этот метод будет использоваться сервисом уведомлений
     * @param stockQuantity Пороговое значение запасов.
     * @return Список продуктов с низким запасом.
     */
    List<Product> findByStockQuantityLessThan(Integer stockQuantity);

    List<Product> findByCategoryId(Long categoryId);
}
