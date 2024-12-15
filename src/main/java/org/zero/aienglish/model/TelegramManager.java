package org.zero.aienglish.model;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface TelegramManager {
    List<BotApiMethod<?>> handle(Update update);
}
