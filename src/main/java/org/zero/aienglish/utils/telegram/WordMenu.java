package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.TelegramCallbackButton;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class WordMenu implements BiFunction<Integer, WordMenu.WordMenuState, InlineKeyboardMarkup> {
    private final InlineKeyboard inlineKeyboard;

    @Override
    public InlineKeyboardMarkup apply(Integer wordId, WordMenuState wordMenuState) {
        return switch (wordMenuState) {
            case SAVED -> createSavedWordMenu(wordId);
            case DEFAULT -> createDefaultWordMenu(wordId);
            case ENDED -> createEndedWordsMenu();
        };
    }

    private InlineKeyboardMarkup createDefaultWordMenu(Integer wordId) {
        List<TelegramCallbackButton> buttons = List.of(
                createNextButton(),
                createSaveButton(wordId)
        );
        return inlineKeyboard.apply(buttons);
    }

    private InlineKeyboardMarkup createSavedWordMenu(Integer wordId) {
        List<TelegramCallbackButton> buttons = List.of(
                createNextButton(),
                createRemoveButton(wordId),
                createDisabledSaveButton()
        );
        return inlineKeyboard.apply(buttons);
    }

    private InlineKeyboardMarkup createEndedWordsMenu() {
        List<TelegramCallbackButton> buttons = List.of(
                createStartPracticeButton()
        );

        return inlineKeyboard.apply(buttons);
    }

    private TelegramCallbackButton createNextButton() {
        return new TelegramCallbackButton(CallbackEnum.NEXT_VOCABULRAY.name(), "Далі");
    }

    private TelegramCallbackButton createSaveButton(Integer wordId) {
        return new TelegramCallbackButton(
                CallbackEnum.ADD_VOCABULARY.name() + " " + wordId,
                "Зберегти"
        );
    }

    private TelegramCallbackButton createRemoveButton(Integer wordId) {
        return new TelegramCallbackButton(
                CallbackEnum.REMOVE.name() + " " + wordId,
                "\uD83E\uDDE8 Видалити"
        );
    }

    private TelegramCallbackButton createStartPracticeButton() {
        return new TelegramCallbackButton(
                CallbackEnum.NEXT_TASK.name(),
                "Почати практику"
        );
    }

    private TelegramCallbackButton createDisabledSaveButton() {
        return new TelegramCallbackButton(
                CallbackEnum.DISABLE.name(),
                "✅ Збережено"
        );
    }

    public enum WordMenuState {
        DEFAULT,
        SAVED,
        ENDED;
    }
}
