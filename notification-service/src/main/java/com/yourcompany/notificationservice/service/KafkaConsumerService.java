package com.yourcompany.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.notificationservice.dto.Product;
import com.yourcompany.notificationservice.telegram.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final TelegramBot telegramBot;
    private final String notificationChatId;

    public KafkaConsumerService(ObjectMapper objectMapper,
                                TelegramBot telegramBot,
                                @Value("${notification.chat.id}") String notificationChatId) {
        this.objectMapper = objectMapper;
        this.telegramBot = telegramBot;
        this.notificationChatId = notificationChatId;
    }

    @KafkaListener(topics = "low-stock-notifications", groupId = "${spring.kafka.consumer.group-id}")
    public void listenLowStock(String message) {
        log.info("Received message from Kafka: {}", message);
        try {
            Product product = objectMapper.readValue(message, Product.class);

            String notificationText = String.format(
                    "Внимание! Заканчивается товар:\n\nID: %d\nНазвание: %s / %s\nОстаток: %d шт.",
                    product.getId(),
                    product.getNameRu(),
                    product.getNameEn(),
                    product.getStockQuantity()
            );

            telegramBot.sendNotification(notificationChatId, notificationText);

        } catch (JsonProcessingException e) {
            log.error("Error deserializing product from Kafka message", e);
        }
    }
}
