package com.yourcompany.notificationservice.telegram;

import com.yourcompany.notificationservice.dto.Category;
import com.yourcompany.notificationservice.dto.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    private static final String BOT_TOKEN = "test-token";
    private static final String BOT_NAME = "test-bot";
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8081";
    private static final long CHAT_ID = 12345L;

    @Mock
    private RestTemplate restTemplate;

    private TelegramBot telegramBot;

    @BeforeEach
    void setUp() {
        // Используем spy, чтобы мокать только метод execute, а остальную логику оставить
        telegramBot = Mockito.spy(new TelegramBot(BOT_TOKEN, BOT_NAME, restTemplate, PRODUCT_SERVICE_URL));
        try {
            // Мокаем метод execute, чтобы он ничего не делал и не бросал исключение
            Mockito.doReturn(null).when(telegramBot).execute(any(SendMessage.class));
        } catch (TelegramApiException e) {
            // Это исключение не будет брошено, так как мы мокаем метод
        }
    }

    private Update createMockUpdate(String text) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        message.setChat(chat);
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    @Test
    void onUpdateReceived_shouldHandleAllProductsCommand() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/allproducts");
        Product product = new Product();
        product.setId(1L);
        product.setNameRu("Тестовый Продукт");
        product.setNameEn("Test Product");
        product.setStockQuantity(10);
        List<Product> productList = Collections.singletonList(product);
        ResponseEntity<List<Product>> responseEntity = new ResponseEntity<>(productList, HttpStatus.OK);

        when(restTemplate.exchange(
                PRODUCT_SERVICE_URL + "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        )).thenReturn(responseEntity);

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Список всех продуктов:\nID: 1, Название: Тестовый Продукт (Test Product), Остаток: 10\n", sentMessage.getText());
    }

    @Test
    void onUpdateReceived_shouldHandleProductByIdCommand() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/product 1");
        Product product = new Product();
        product.setId(1L);
        product.setNameEn("Test Product");
        product.setNameRu("Тестовый Продукт");
        product.setCharacteristics("Характеристики");
        product.setWeight(1.5);
        product.setSize("10x20x30");
        product.setStockQuantity(5);
        Category category = new Category();
        category.setName("Тестовая Категория");
        product.setCategory(category);

        when(restTemplate.getForObject(PRODUCT_SERVICE_URL + "/api/products/1", Product.class)).thenReturn(product);

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        String expectedText = "Информация о продукте ID 1:\n" +
                "- Название: Test Product / Тестовый Продукт\n" +
                "- Характеристики: Характеристики\n" +
                "- Вес: 1.5 кг\n" +
                "- Размер: 10x20x30\n" +
                "- Срок годности: null\n" +
                "- Остаток: 5 шт.\n" +
                "- Категория: Тестовая Категория\n";
        assertEquals(expectedText, sentMessage.getText());
    }

    @Test
    void onUpdateReceived_shouldHandleProductNotFound() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/product 99");
        when(restTemplate.getForObject(PRODUCT_SERVICE_URL + "/api/products/99", Product.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Не удалось получить информацию о продукте. Попробуйте позже.", sentMessage.getText());
    }

    @Test
    void onUpdateReceived_shouldHandleInvalidProductIdFormat() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/product abc");

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Неверный формат ID. Пожалуйста, введите число.", sentMessage.getText());
    }

    @Test
    void onUpdateReceived_shouldHandleUnknownCommand() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/unknown");

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Неизвестная команда. Доступные команды: /allproducts, /product <id>", sentMessage.getText());
    }

    @Test
    void sendNotification_shouldExecuteSendMessage() throws TelegramApiException {
        // Given
        String chatId = "54321";
        String text = "Test Notification";

        // When
        telegramBot.sendNotification(chatId, text);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(chatId, sentMessage.getChatId());
        assertEquals(text, sentMessage.getText());
    }

    @Test
    void onUpdateReceived_shouldHandleProductCommandWithoutId() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/product");

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Пожалуйста, укажите ID продукта. Пример: /product 1", sentMessage.getText());
    }

    @Test
    void handleAllProductsCommand_shouldHandleEmptyList() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/allproducts");
        ResponseEntity<List<Product>> responseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        when(restTemplate.exchange(
                PRODUCT_SERVICE_URL + "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        )).thenReturn(responseEntity);

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Продукты не найдены.", sentMessage.getText());
    }

    @Test
    void handleAllProductsCommand_shouldHandleException() throws TelegramApiException {
        // Given
        Update update = createMockUpdate("/allproducts");
        when(restTemplate.exchange(
                PRODUCT_SERVICE_URL + "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        )).thenThrow(new RuntimeException("Service unavailable"));

        // When
        telegramBot.onUpdateReceived(update);

        // Then
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertEquals(String.valueOf(CHAT_ID), sentMessage.getChatId());
        assertEquals("Не удалось получить список продуктов. Попробуйте позже.", sentMessage.getText());
    }
}
