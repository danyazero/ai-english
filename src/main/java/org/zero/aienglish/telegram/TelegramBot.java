package org.zero.aienglish.telegram;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.config.TelegramProperties;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient;
    private final TelegramProperties botProperties;
    private final CommandController commandsController;
    private final CallbackController callbackController;

    @PostConstruct
    private void init() {
        this.telegramClient = new OkHttpTelegramClient(botProperties.getToken());
    }

    @Override
    public void consume(Update update) {
        log.info("Telegram bot receive update");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            log.info("Getted message in chat -> {}", chatId);
            if (update.getMessage().getText().startsWith("/")) sendMessage(commandsController.handle(update));
        } else if (update.hasCallbackQuery()) {
            log.info("Has callback query -> {}", update.getCallbackQuery().getData().toString());
            sendMessage(callbackController.handle(update));
        }
    }

    private void sendMessage(List<BotApiMethod<?>> updates) {
        try {
            for (BotApiMethod<?> update : updates) {
                telegramClient.execute(update);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
//
