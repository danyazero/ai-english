package org.zero.aienglish.model;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface TelegramCallback {
    String getName();
    List<BotApiMethod<?>> apply(Update update);
}
