package org.zero.aienglish.utils.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.zero.aienglish.model.TelegramCallbackButton;

import java.util.List;
import java.util.function.Function;

@Component
public class InlineKeyboard implements Function<List<TelegramCallbackButton>, InlineKeyboardMarkup> {
    @Override
    public InlineKeyboardMarkup apply(List<TelegramCallbackButton> callbackButtons) {
        var keyboardMarkup = InlineKeyboardMarkup.builder();
        for (TelegramCallbackButton callbackButton : callbackButtons) {
            var newKeyboardRow = new InlineKeyboardRow(
                    InlineKeyboardButton
                            .builder()
                            .text(callbackButton.text())
                            .callbackData(callbackButton.callback())
                            .build()
            );
            keyboardMarkup.keyboardRow(newKeyboardRow);
        }

        return keyboardMarkup.build();
    }
}
