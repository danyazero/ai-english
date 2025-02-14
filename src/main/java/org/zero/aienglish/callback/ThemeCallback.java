package org.zero.aienglish.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.TelegramCallbackEnum;
import org.zero.aienglish.service.ThemeService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.ThemeMenu;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThemeCallback implements TelegramCallback {
    private final ThemeMenu themeMenu;
    private final UserService userService;
    private final ThemeService themeService;

    @Override
    public String getName() {
        return TelegramCallbackEnum.THEME.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        String[] splittedCallback = update.getCallbackQuery().getData().split(" ");
        var themeId = Integer.parseInt(splittedCallback[1]);
        var categoryId = Integer.parseInt(splittedCallback[2]);

        var themes = themeService.selectTheme(serverUserId, themeId);
        log.info("Current selected themes -> {}", themes);

        if (themes.isEmpty()) return List.of();

        var keyboard = themeMenu.apply(ThemeMenu.ThemeMenuState.THEME, categoryId, themes.get().recommendations(), true);

        List<String> themeTitles = themes.get().saved().stream()
                .map(element -> "• " + element.title())
                .toList();

        String bulletListOfThemes = String.join("\n", themeTitles);

        var editMessageText = EditMessageText.builder()
                .text("Оберіть теми для практики." + (bulletListOfThemes.isBlank() ? "" : "\n\nОбрані теми:\n" + bulletListOfThemes))
                .messageId(messageId)
                .chatId(chatId)
                .build();

        var editKeyboardMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(keyboard)
                .messageId(messageId)
                .chatId(chatId)
                .build();

        return List.of(editMessageText, editKeyboardMarkup);
    }
}
