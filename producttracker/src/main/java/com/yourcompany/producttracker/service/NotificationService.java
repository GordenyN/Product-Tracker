package com.yourcompany.producttracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.producttracker.model.Product;
import com.yourcompany.producttracker.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для отправки уведомлений в Kafka.
 * Использует планировщик Spring для периодической проверки запасов.
 */
@Service
@Slf4j
public class NotificationService {

    private static final String LOW_STOCK_TOPIC = "low-stock-notifications";

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public NotificationService(ProductRepository productRepository,
                               KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${low.stock.threshold}")
    private int lowStockThreshold;

    /**
     * Метод, который выполняется по расписанию.
     * Ищет товары с низким остатком и отправляет информацию о каждом в топик Kafka.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void checkLowStockAndNotify() {
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(lowStockThreshold);

        if (!lowStockProducts.isEmpty()) {
            log.info("Found {} products with low stock. Sending to Kafka topic '{}'...", lowStockProducts.size(), LOW_STOCK_TOPIC);
            for (Product product : lowStockProducts) {
                try {
                    String productJson = objectMapper.writeValueAsString(product);
                    kafkaTemplate.send(LOW_STOCK_TOPIC, product.getId().toString(), productJson);
                } catch (JsonProcessingException e) {
                    log.error("Error serializing product with ID {} to JSON", product.getId(), e);
                }
            }
        }
    }
}
