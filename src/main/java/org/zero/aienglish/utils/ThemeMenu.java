package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.zero.aienglish.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThemeMenu {
    private final TelegramInlineKeyboard inlineKeyboard;

    public InlineKeyboardMarkup apply(ThemeMenuState themeMenuState, Integer selectedCategory, Pagination<ThemeDTO> themes, boolean isSavedThemes) {

        return switch (themeMenuState) {
            case DEFAULT -> createCategoriesMenu(themes.items(), isSavedThemes);
            case THEME -> createThemesMenu(selectedCategory, themes);
        };
    }

    private InlineKeyboardMarkup createThemesMenu(Integer selectedCategory, Pagination<ThemeDTO> themes) {

        List<TelegramCallbackButton> buttons = themes.items().stream()
                .map(element -> getThemeButtonFromThemeDTO(element, selectedCategory))
                .collect(Collectors.toList());

        buttons.add(
                new TelegramCallbackButton(
                        TelegramCallbackEnum.BACK_THEME.name(),
                        ReplicEnum.PREV.getValue()
                )
        );

        var themeButtons = inlineKeyboard.apply(buttons);

        log.info("Current page -> {}, Amount pages -> {}", themes.currentPage(), themes.totalPages());
        if (isShouldBePagination(themes)) {
            log.info("Rendering pagination");
            var keyboard = InlineKeyboardMarkup.builder()
                    .keyboard(themeButtons.getKeyboard());
            var paginationRow = new InlineKeyboardRow();

            addNextPageCallbackButton(themes, paginationRow, selectedCategory, themes.currentPage());
            addPrevPageCallbackButton(themes, paginationRow, selectedCategory, themes.currentPage());
            keyboard.keyboardRow(paginationRow);

            return keyboard.build();
        }

        return themeButtons;
    }

    private static void addNextPageCallbackButton(Pagination<ThemeDTO> retrievedThemes, InlineKeyboardRow paginationRow, Integer categoryId, Integer page) {
        if (retrievedThemes.currentPage() + 1 < retrievedThemes.totalPages()) {
            log.info("Rendering next page button");
            var nextPageButton = InlineKeyboardButton
                    .builder()
                    .text(">>")
                    .callbackData(TelegramCallbackEnum.NEXT_THEME.name() + " " + categoryId + " " + (page + 1))
                    .build();
            paginationRow.add(nextPageButton);
        }
    }

    private static void addPrevPageCallbackButton(Pagination<ThemeDTO> retrievedThemes, InlineKeyboardRow paginationRow, Integer categoryId, Integer page) {
        if (retrievedThemes.currentPage() - 1 >= 0) {
            log.info("Rendering previous page button");
            var prevPageButton = InlineKeyboardButton.builder()
                    .text("<<")
                    .callbackData(TelegramCallbackEnum.PREV_THEME.name() + " " + categoryId + " " + (page - 1))
                    .build();
            paginationRow.add(prevPageButton);
        }
    }

    private static boolean isShouldBePagination(Pagination<ThemeDTO> retrievedThemes) {
        return retrievedThemes.currentPage() < retrievedThemes.totalPages() && retrievedThemes.currentPage() >= 0;
    }


    private InlineKeyboardMarkup createCategoriesMenu(List<ThemeDTO> themes, boolean isSavedThemes) {

        List<TelegramCallbackButton> buttons = themes.stream()
                .map(this::getCategoryButtonFromThemeDTO)
                .collect(Collectors.toList());

        if (isSavedThemes) {
            buttons.add(getClearThemesButton());
        }

        return inlineKeyboard.apply(buttons);
    }

    private static TelegramCallbackButton getClearThemesButton() {
        return new TelegramCallbackButton(
                TelegramCallbackEnum.CLEAR_THEMES.name(),
                "\uD83E\uDDE8 Очистити"
        );
    }

    private TelegramCallbackButton getThemeButtonFromThemeDTO(ThemeDTO theme, Integer categoryId) {
        return new TelegramCallbackButton(
                TelegramCallbackEnum.THEME.name() + " " + theme.id() + " " + categoryId,
                (theme.isSelected() ? "✅ " : "") + theme.title()
        );
    }

    private TelegramCallbackButton getCategoryButtonFromThemeDTO(ThemeDTO theme) {
        return new TelegramCallbackButton(
                TelegramCallbackEnum.CATEGORY.name() + " " + theme.id(),
                theme.title()
        );
    }

    public enum ThemeMenuState {
        DEFAULT,
        THEME
    }
}
