package com.yourcompany.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.notificationservice.dto.Product;
import com.yourcompany.notificationservice.telegram.TelegramBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TelegramBot telegramBot;

    private KafkaConsumerService kafkaConsumerService;

    private final String TEST_NOTIFICATION_CHAT_ID = "testChatId";

    @BeforeEach
    public void setUp() {
        kafkaConsumerService = new KafkaConsumerService(objectMapper, telegramBot, TEST_NOTIFICATION_CHAT_ID);
    }

    @Test
    public void testListenLowStock() throws JsonProcessingException {
        // Given
        String message = "{\"id\":1,\"nameRu\":\"Телефон\",\"nameEn\":\"Phone\",\"stockQuantity\":5}";
        Product product = new Product();
        product.setId(1L);
        product.setNameRu("Телефон");
        product.setNameEn("Phone");
        product.setStockQuantity(5);

        when(objectMapper.readValue(message, Product.class)).thenReturn(product);

        // When
        kafkaConsumerService.listenLowStock(message);

        // Then
        verify(telegramBot, times(1)).sendNotification(anyString(), anyString());
    }

    @Test
    public void testListenLowStock_JsonProcessingException() throws JsonProcessingException {
        // Given
        String message = "invalid-json";
        when(objectMapper.readValue(message, Product.class)).thenThrow(new JsonProcessingException(""){});

        // When
        kafkaConsumerService.listenLowStock(message);

        // Then
        verify(telegramBot, never()).sendNotification(anyString(), anyString());
    }
}
