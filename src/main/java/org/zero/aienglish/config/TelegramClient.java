package org.zero.aienglish.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Component
@RequiredArgsConstructor
public class TelegramClient {
    private final TelegramProperties botProperties;
    @Getter
    private org.telegram.telegrambots.meta.generics.TelegramClient telegramClient;

    @PostConstruct
    private void init() {
        this.telegramClient = new OkHttpTelegramClient(botProperties.getToken());
    }

}
