package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.zero.aienglish.config.TelegramBot;
import org.zero.aienglish.config.TelegramClient;
import org.zero.aienglish.model.NotificationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final TelegramBot telegramBot;
    private final UserService userService;
    private final TelegramClient telegramClient;

    public void sendNotification(NotificationDTO notification) {
        log.info("Send notification : {}", notification);
        var foundedUser = userService.getUserTelegramId(notification.recipientId());
        if (foundedUser.isEmpty()) {
            log.warn("Can't send notification '{}' for user with id -> {}, because is not found.", notification.message(), notification.recipientId());
            return;
        }

        var message = SendMessage.builder()
                .chatId(foundedUser.get())
                .text(notification.message())
                .parseMode("HTML")
                .build();

        try {
            telegramClient.getTelegramClient().execute(message);
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
        }

    }
}
