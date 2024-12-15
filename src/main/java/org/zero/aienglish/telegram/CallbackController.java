package org.zero.aienglish.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.model.TelegramManager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CallbackController implements TelegramManager {
    private final Map<String, TelegramCallback> callbacks;

    public CallbackController(List<TelegramCallback> callbacks) {
        this.callbacks = callbacks.stream()
                .collect(Collectors.toMap(
                        TelegramCallback::getName,
                        Function.identity()
                ));
    }


    @Override
    public List<BotApiMethod<?>> handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        log.info("Handle callback in chat -> {}", chatId);
        String callback = update.getCallbackQuery().getData();
        log.info("Parsed next callback -> {}", callback);

        var commandHandler = callbacks.get(callback.split(" ")[0]);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return List.of();
        }

    }
}
