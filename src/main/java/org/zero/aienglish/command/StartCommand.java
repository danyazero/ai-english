package org.zero.aienglish.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.model.ReplicEnum;
import org.zero.aienglish.model.TelegramCallbackEnum;
import org.zero.aienglish.model.TelegramCommand;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements TelegramCommand {

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();

        var inlineKeyboardButton = InlineKeyboardButton.builder()
                .text(ReplicEnum.PRACTICE.getValue())
                .callbackData(TelegramCallbackEnum.PRACTICE_TASK.name())
                .build();

        var keyboardRow = new InlineKeyboardRow(inlineKeyboardButton);

        var startMessageInlineKeyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(ReplicEnum.START.getValue())
                .replyMarkup(startMessageInlineKeyboard)
                .build();

        return List.of(
                message
        );
    }
}
