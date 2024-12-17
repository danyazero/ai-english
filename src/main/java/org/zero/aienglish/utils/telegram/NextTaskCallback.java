package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.TelegramButton;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.service.TaskService;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NextTaskCallback implements TelegramCallback {
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final Keyboard keyboard;

    @Override
    public String getName() {
        return CallbackEnum.NEXT_TASK.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var userId = userRepository.findFirstByTelegramId(chatId).getId();

        var task = taskService.getTask(userId);

        var buttons = task.answers().stream()
                .map(elem -> new TelegramButton(0, elem.getWord()))
                .toList();

        var keyboardMarkup = keyboard.apply(buttons);

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(task.title())
                .replyMarkup(keyboardMarkup)
                .build();

        return List.of(message);
    }
}