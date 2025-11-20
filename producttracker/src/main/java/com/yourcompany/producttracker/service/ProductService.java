package com.yourcompany.producttracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.producttracker.dto.ProductRequestDto;
import com.yourcompany.producttracker.dto.ProductResponseDto;
import com.yourcompany.producttracker.model.Product;
import com.yourcompany.producttracker.repository.ProductRepository;
import com.yourcompany.producttracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final String LOW_STOCK_TOPIC = "low-stock-notifications";

    // Внедрение зависимости ProductRepository через конструктор
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Преобразует ProductRequestDto в сущность Product.
     * @param dto ProductRequestDto с данными для создания/обновления.
     * @return Сущность Product.
     */
    public Product toEntity(ProductRequestDto dto) {
        Product product = new Product();
        product.setNameEn(dto.getNameEn());
        product.setNameRu(dto.getNameRu());
        product.setCharacteristics(dto.getCharacteristics());
        product.setWeight(dto.getWeight());
        product.setSize(dto.getSize());
        product.setExpiryDate(dto.getExpiryDate());
        product.setStockQuantity(dto.getStockQuantity());
        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId()).ifPresent(product::setCategory);
        }
        return product;
    }

    /**
     * Преобразует сущность Product в ProductResponseDto.
     * @param product Сущность Product.
     * @return ProductResponseDto.
     */
    public ProductResponseDto toDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setNameEn(product.getNameEn());
        dto.setNameRu(product.getNameRu());
        dto.setCharacteristics(product.getCharacteristics());
        dto.setWeight(product.getWeight());
        dto.setSize(product.getSize());
        dto.setExpiryDate(product.getExpiryDate());
        dto.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }

    // Получить все продукты
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Получить продукт по ID
    public Optional<ProductResponseDto> getProductById(Long id) {
        return productRepository.findById(id).map(this::toDto);
    }

    // Создать новый продукт
    public ProductResponseDto createProduct(ProductRequestDto productDto) {
        Product product = toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        checkStockAndSendNotification(savedProduct);
        return toDto(savedProduct);
    }

    // Обновить существующий продукт
    public Optional<ProductResponseDto> updateProduct(Long id, ProductRequestDto productDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setNameEn(productDto.getNameEn());
                    existingProduct.setNameRu(productDto.getNameRu());
                    existingProduct.setCharacteristics(productDto.getCharacteristics());
                    existingProduct.setWeight(productDto.getWeight());
                    existingProduct.setSize(productDto.getSize());
                    existingProduct.setExpiryDate(productDto.getExpiryDate());
                    existingProduct.setStockQuantity(productDto.getStockQuantity());
                    if (productDto.getCategoryId() != null) {
                        categoryRepository.findById(productDto.getCategoryId()).ifPresent(existingProduct::setCategory);
                    }
                    Product updatedProduct = productRepository.save(existingProduct);
                    checkStockAndSendNotification(updatedProduct);
                    return toDto(updatedProduct);
                });
    }

    // Удалить продукт
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<ProductResponseDto> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void checkStockAndSendNotification(Product product) {
        if (product.getStockQuantity() < LOW_STOCK_THRESHOLD) {
            try {
                String productJson = objectMapper.writeValueAsString(toDto(product));
                kafkaTemplate.send(LOW_STOCK_TOPIC, productJson);
                log.info("Sent low stock notification for product: {}", product.getNameRu());
            } catch (JsonProcessingException e) {
                log.error("Error serializing product to JSON", e);
            }
        }
    }
}


