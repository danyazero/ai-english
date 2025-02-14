package org.zero.aienglish.callback;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.service.TaskService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.TelegramKeyboard;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NextTaskCallback implements TelegramCallback {
    private final TaskService taskService;
    private final UserService userService;
    private final TelegramKeyboard tgKeyboard;

    @Override
    public String getName() {
        return TelegramCallbackEnum.PRACTICE_TASK.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var callback = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .showAlert(false)
                .build();

        TaskCheckResult task;
        try {
            task = taskService.checkTask(serverUserId, "");
        } catch (StatusRuntimeException exception) {
            var message = SendMessage.builder()
                    .chatId(chatId)
                    .text(exception.getMessage())
                    .build();

            return List.of(message, callback);
        }

        if (task.state() == null) {
            var message = SendMessage.builder()
                    .text(ReplicEnum.TASK_NOT_FOUND.getValue())
                    .disableNotification(true)
                    .chatId(chatId)
                    .build();

            return List.of(message, callback);
        }

        var answers = task.state().answers().stream().map(TaskAnswer::word).toList();
        var keyboard = tgKeyboard.apply(answers);


        var message = SendMessage.builder()
                .text("<b>Завдання:</b>\n" + task.state().title() + "\n\n<b>Переклад:</b>\n<span class=\"tg-spoiler\">" + task.state().caption() + "</span>")
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .disableNotification(true)
                .chatId(chatId)
                .build();


        return List.of(message, callback);
    }
}
