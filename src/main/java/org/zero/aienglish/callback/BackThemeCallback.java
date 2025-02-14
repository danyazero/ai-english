package org.zero.aienglish.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.TelegramCallbackEnum;
import org.zero.aienglish.service.ThemeService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.ThemeMenu;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackThemeCallback implements TelegramCallback {
    private final ThemeMenu themeMenu;
    private final ThemeService themeService;
    private final UserService userService;

    @Override
    public String getName() {
        return TelegramCallbackEnum.BACK_THEME.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);


        var themes = themeService.getThemeCategories(serverUserId);


        var keyboard = themeMenu.apply(ThemeMenu.ThemeMenuState.DEFAULT, null, themes.recommendations(), !themes.saved().isEmpty());

        var editKeyboardMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(keyboard)
                .messageId(messageId)
                .chatId(chatId)
                .build();

        return List.of(editKeyboardMarkup);
    }
}
