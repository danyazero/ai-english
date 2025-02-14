package org.zero.aienglish.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.TelegramCallbackEnum;
import org.zero.aienglish.model.ThemeDTO;
import org.zero.aienglish.service.ThemeService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.ThemeMenu;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCallback implements TelegramCallback {
    private final ThemeMenu themeMenu;
    private final UserService userService;
    private final ThemeService themeService;

    @Override
    public String getName() {
        return TelegramCallbackEnum.CATEGORY.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var categoryId = Integer.parseInt(update.getCallbackQuery().getData().split(" ")[1]);

        var themes = themeService.getThemeForCategory(serverUserId, categoryId, 0);
        log.info("Current selected themes -> {}", themes.items().stream().map(ThemeDTO::id).toList());
        var keyboard = themeMenu.apply(ThemeMenu.ThemeMenuState.THEME, categoryId, themes, true);

        var editKeyboardMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(keyboard)
                .messageId(messageId)
                .chatId(chatId)
                .build();

        return List.of(editKeyboardMarkup);
    }
}
