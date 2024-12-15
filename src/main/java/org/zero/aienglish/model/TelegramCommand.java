package org.zero.aienglish.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommand {
    String getName();
    SendMessage apply(Update update);
}
