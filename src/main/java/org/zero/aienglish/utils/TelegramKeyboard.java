package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.function.Function;

@Component
public class TelegramKeyboard implements Function<List<String>, ReplyKeyboardMarkup> {
    @Override
    public ReplyKeyboardMarkup apply(List<String> buttons) {
        var keyboardMarkup = ReplyKeyboardMarkup.builder();
        if (buttons.size() <= 3)  addButtonsLessThanFour(buttons, keyboardMarkup);
        else addButtonsGreaterThanFour(buttons, keyboardMarkup);

        return keyboardMarkup
                .resizeKeyboard(true)
                .build();
    }
    private static void addButtonsGreaterThanFour(List<String> buttons, ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder<?, ? extends ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder<?, ?>> keyboardMarkup) {
        for (int i = 0; i < buttons.size(); i+=2) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = 0; j < 2; j++) {
                var buttonIndex = i+j;
                if (buttonIndex < buttons.size()) keyboardRow.add(buttons.get(buttonIndex));
            }
            keyboardMarkup.keyboardRow(keyboardRow);
        }
    }

    private static void addButtonsLessThanFour(List<String> buttons, ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder<?, ? extends ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder<?, ?>> keyboardMarkup) {
        for (String button : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(button);

            keyboardMarkup.keyboardRow(keyboardRow);
        }
    }
}
