package org.zero.aienglish.model;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public interface TelegramCommand {
    String getName();
    List<BotApiMethod<?>> apply(Update update, TelegramClient client);
}
