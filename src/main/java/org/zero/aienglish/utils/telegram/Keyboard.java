package org.zero.aienglish.utils.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.zero.aienglish.model.TelegramButton;

import java.util.List;
import java.util.function.Function;

@Component
public class Keyboard implements Function<List<TelegramButton>, ReplyKeyboardMarkup> {
    @Override
    public ReplyKeyboardMarkup apply(List<TelegramButton> buttons) {
        var keyboardMarkup = ReplyKeyboardMarkup.builder();
        for (TelegramButton button : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(button.text());

            keyboardMarkup.keyboardRow(keyboardRow);
        }

        return keyboardMarkup.build();
    }
}
