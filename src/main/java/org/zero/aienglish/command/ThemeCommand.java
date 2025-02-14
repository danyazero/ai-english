package org.zero.aienglish.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.model.ThemeDTO;
import org.zero.aienglish.service.ThemeService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.ThemeMenu;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThemeCommand implements TelegramCommand {
    private final ThemeService themeService;
    private final UserService userService;
    private final ThemeMenu themeMenu;

    @Override
    public String getName() {
        return "/theme";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();
        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var themes = themeService.getThemeCategories(serverUserId);

        log.info("Current selected themes -> {}", themes.saved().stream().map(ThemeDTO::id).toList());
        var keyboard = themeMenu.apply(ThemeMenu.ThemeMenuState.DEFAULT, null, themes.recommendations(), !themes.saved().isEmpty());


        List<String> themeTitles = themes.saved().stream()
                .map(element -> "• " + element.title())
                .toList();

        String bulletListOfThemes = String.join("\n",  themeTitles);
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("Оберіть теми для практики." + (bulletListOfThemes.isBlank() ? "" : "\n\nОбрані теми:\n" + bulletListOfThemes))
                .replyMarkup(keyboard)
                .build();

        return List.of(
                message
        );
    }
}
