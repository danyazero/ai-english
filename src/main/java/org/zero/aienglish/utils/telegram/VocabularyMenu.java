package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.ReplicEnum;
import org.zero.aienglish.model.TelegramCallbackButton;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class VocabularyMenu implements BiFunction<Integer, Integer, InlineKeyboardMarkup> {
    private final InlineKeyboard inlineKeyboard;

    public InlineKeyboardMarkup apply(Integer currentPage, Integer totalPages) {
        if (currentPage > 0 && currentPage < totalPages-1) return getDefaultMenu(currentPage);
        else if (currentPage <= 0 && currentPage < totalPages-1) return getNextMenu(currentPage);

        return getPreviousMenu(currentPage);
    }

    private InlineKeyboardMarkup getDefaultMenu(Integer page) {
        return inlineKeyboard.apply(
                List.of(
                        createPreviousButton(page),
                        createNextButton(page)
                )
        );
    }

    private InlineKeyboardMarkup getPreviousMenu(Integer page) {
        return inlineKeyboard.apply(
                List.of(
                        createPreviousButton(page)
                )
        );
    }

    private InlineKeyboardMarkup getNextMenu(Integer page) {
        return inlineKeyboard.apply(
                List.of(
                        createNextButton(page)
                )
        );
    }

    private TelegramCallbackButton createNextButton(Integer page) {
        return new TelegramCallbackButton(
                CallbackEnum.VOCABULARY_PAGE.name() + " " + (page + 1),
                ReplicEnum.NEXT.getValue()
        );
    }

    private TelegramCallbackButton createPreviousButton(Integer page) {
        return new TelegramCallbackButton(
                CallbackEnum.VOCABULARY_PAGE.name() + " " + (page - 1),
                ReplicEnum.PREV.getValue()
        );
    }


    public enum VocabularyMenuState {
        DEFAULT,
        NEXT,
        PREV,
        NONE
    }
}
