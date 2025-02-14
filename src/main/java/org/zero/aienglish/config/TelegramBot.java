package org.zero.aienglish.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.zero.aienglish.telegram_controller.CallbackController;
import org.zero.aienglish.telegram_controller.CommandController;
import org.zero.aienglish.telegram_controller.PlainTextController;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final CommandController commandController;
    private final CallbackController callbackController;
    private final PlainTextController plainTextController;


    @Override
    public void consume(Update update) {
        log.info("Telegram bot receive update");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            log.info("Getted message in chat -> {}", chatId);
            if (update.getMessage().getText().startsWith("/"))
                sendMessage(commandController.handle(update, telegramClient.getTelegramClient()));
            else sendMessage(plainTextController.handle(update, telegramClient.getTelegramClient()));

        } else if (update.hasCallbackQuery()) {
            log.info("Has callback query -> {}", update.getCallbackQuery().getData().toString());
            sendMessage(callbackController.handle(update, telegramClient.getTelegramClient()));
        }
    }

    public void sendMessage(BotApiMethod<?> update) {
    }

    private void sendMessage(List<BotApiMethod<?>> updates) {
        try {
            for (BotApiMethod<?> update : updates) {
                telegramClient.getTelegramClient().execute(update);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
