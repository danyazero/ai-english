package org.zero.aienglish.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TaskAnswer;
import org.zero.aienglish.model.TaskState;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.service.TaskService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.TelegramKeyboard;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevertCommand implements TelegramCommand {
    private final TaskService taskService;
    private final UserService userService;
    private final TelegramKeyboard tgKeyboard;

    @Override
    public String getName() {
        return "/revert";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var revertedTaskState = taskService.revert(serverUserId);

        if (revertedTaskState.isEmpty()) {
            log.warn("It seems like task not found.");
            var message = SendMessage.builder()
                    .text("Зараз немає активних завдань.")
                    .chatId(chatId)
                    .build();

            return List.of(message);
        }

        var formattedAnswers = revertedTaskState.get().answers().stream()
                .map(TaskAnswer::word)
                .toList();

        var textKeyboard = tgKeyboard.apply(formattedAnswers);

        var stepStateMessage = getStepStateMessage(revertedTaskState.get(), textKeyboard, chatId);

        return List.of(stepStateMessage);
    }

    private SendMessage getStepStateMessage(TaskState taskState, ReplyKeyboard keyboard, Long chatId) {
        return SendMessage.builder()
                .text(getStepMessage(taskState))
                .disableNotification(true)
                .replyMarkup(keyboard)
                .parseMode("HTML")
                .chatId(chatId)
                .build();
    }

    private String getStepMessage(TaskState taskState) {
        return taskState.title() + "\nКрок " + taskState.currentStep() + "/" + taskState.amountSteps();
    }
}
