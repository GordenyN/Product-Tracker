package com.yourcompany.notificationservice.telegram;

import com.yourcompany.notificationservice.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger a_log = LoggerFactory.getLogger(TelegramBot.class);

    private final String botName;
    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.username}") String botName,
                       RestTemplate restTemplate,
                       @Value("${product.service.url}") String productServiceUrl) {
        super(botToken);
        this.botName = botName;
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/allproducts".equals(text)) {
                handleAllProductsCommand(chatId);
            } else if (text.startsWith("/product")) {
                handleProductByIdCommand(chatId, text);
            } else {
                sendDefaultResponse(chatId);
            }
        }
    }

    private void handleProductByIdCommand(long chatId, String text) {
        try {
            String[] parts = text.split(" ");
            if (parts.length < 2) {
                sendMessage(chatId, "Пожалуйста, укажите ID продукта. Пример: /product 1");
                return;
            }
            Long id = Long.parseLong(parts[1]);
            Product product = restTemplate.getForObject(productServiceUrl + "/api/products/" + id, Product.class);

            if (product == null) {
                sendMessage(chatId, "Продукт с ID " + id + " не найден.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Информация о продукте ID ").append(product.getId()).append(":\n");
            sb.append("- Название: ").append(product.getNameEn()).append(" / ").append(product.getNameRu()).append("\n");
            sb.append("- Характеристики: ").append(product.getCharacteristics()).append("\n");
            sb.append("- Вес: ").append(product.getWeight()).append(" кг\n");
            sb.append("- Размер: ").append(product.getSize()).append("\n");
            sb.append("- Срок годности: ").append(product.getExpiryDate() != null ? product.getExpiryDate().toString() : "null").append("\n");
            sb.append("- Остаток: ").append(product.getStockQuantity()).append(" шт.\n");
            if (product.getCategory() != null) {
                sb.append("- Категория: ").append(product.getCategory().getName()).append("\n");
            }
            sendMessage(chatId, sb.toString());

        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат ID. Пожалуйста, введите число.");
        } catch (HttpClientErrorException.NotFound e) {
            sendMessage(chatId, "Продукт с указанным ID не найден.");
        } catch (Exception e) {
            a_log.error("Failed to fetch product by ID from product-service", e);
            sendMessage(chatId, "Не удалось получить информацию о продукте. Попробуйте позже.");
        }
    }

    private void handleAllProductsCommand(long chatId) {
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    productServiceUrl + "/api/products",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {}
            );

            List<Product> products = response.getBody();
            if (products == null || products.isEmpty()) {
                sendMessage(chatId, "Продукты не найдены.");
                return;
            }

            StringBuilder sb = new StringBuilder("Список всех продуктов:\n");
            for (Product product : products) {
                sb.append("ID: ").append(product.getId())
                  .append(", Название: ").append(product.getNameRu())
                  .append(" (").append(product.getNameEn()).append(")")
                  .append(", Остаток: ").append(product.getStockQuantity()).append("\n");
            }
            sendMessage(chatId, sb.toString());

        } catch (Exception e) {
            a_log.error("Failed to fetch products from product-service", e);
            sendMessage(chatId, "Не удалось получить список продуктов. Попробуйте позже.");
        }
    }

    private void sendDefaultResponse(long chatId) {
        sendMessage(chatId, "Неизвестная команда. Доступные команды: /allproducts, /product <id>");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            a_log.error("Failed to send message to chat {}: {}", chatId, e.getMessage());
        }
    }

    public void sendNotification(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
            a_log.info("Sent notification to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            a_log.error("Failed to send notification to chat {}: {}", chatId, e.getMessage());
        }
    }
}
