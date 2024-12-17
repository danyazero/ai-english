package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.ReplicEnum;
import org.zero.aienglish.model.TelegramCallbackButton;
import org.zero.aienglish.model.TelegramCommand;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommand implements TelegramCommand {
    private final InlineKeyboard inlineKeyboard;
    private static final Logger log = LoggerFactory.getLogger(StartCommand.class);

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        log.info("Called start command handler for chat -> {}", update.getMessage().getChatId());
        var chatId = update.getMessage().getChatId();

        var callbackButtons = inlineKeyboard.apply(List.of(
                new TelegramCallbackButton(CallbackEnum.NEXT_VOCABULRAY.name(), "Обрати нові слова"),
                new TelegramCallbackButton(CallbackEnum.NEXT_TASK.name(), "Почати практику")
        ));

        return List.of(
                SendMessage.builder()
                        .text(ReplicEnum.START.getValue())
                        .replyMarkup(callbackButtons)
                        .chatId(chatId)
                        .build()
        );
    }
}
