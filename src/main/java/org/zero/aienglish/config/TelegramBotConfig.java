package org.zero.aienglish.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Configuration
public class TelegramBotConfig {

    public TelegramBotConfig(
            TelegramProperties telegramProperties,
            TelegramBot telegramBot
    ) throws TelegramApiException {
        log.info("TelegramBotConfig init");
        var bot = new TelegramBotsLongPollingApplication();
        bot.registerBot(telegramProperties.getToken(), telegramBot);
        log.info("Telegram successfully initialized");
    }
}
