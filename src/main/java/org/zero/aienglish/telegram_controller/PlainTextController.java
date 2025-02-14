package org.zero.aienglish.telegram_controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.service.TaskService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.TelegramKeyboard;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlainTextController implements TelegramManager {
    private final TaskService taskService;
    private final UserService userService;
    private final TelegramKeyboard telegramKeyboard;

    @Override
    public List<BotApiMethod<?>> handle(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();


        UserDTO user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);


        var taskState = taskService.checkTask(serverUserId, update.getMessage().getText());

        if (taskState.result() != null && taskState.state() != null) {

            var nextKeyboardButton = InlineKeyboardButton.builder()
                    .text("Далі")
                    .callbackData(TelegramCallbackEnum.PRACTICE_TASK.name())
                    .build();

            var nextTaskRow = new InlineKeyboardRow(nextKeyboardButton);

            var nextTaskKeyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(nextTaskRow)
                    .build();

            var stepStateMessage = getStepStateMessage(taskState.state(), new ReplyKeyboardRemove(true), chatId);
            String formattedMessageText = getFormattedMessageText(taskState.result());

            var message = SendMessage.builder()
                    .text(formattedMessageText)
                    .replyMarkup(nextTaskKeyboard)
                    .disableNotification(true)
                    .parseMode("HTML")
                    .chatId(chatId)
                    .build();

            return List.of(stepStateMessage, message);
        }

        if (taskState.state() == null) {
            var message = SendMessage.builder()
                    .text(ReplicEnum.TASK_NOT_FOUND.getValue())
                    .disableNotification(true)
                    .chatId(chatId)
                    .build();

            return List.of(message);
        }

        var formattedAnswers = taskState.state().answers().stream()
                    .map(TaskAnswer::word)
                    .toList();

        var keyboard = telegramKeyboard.apply(formattedAnswers);

        var stepStateMessage = getStepStateMessage(taskState.state(), keyboard, chatId);

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
        return taskState.title() + "\n\nКрок " + taskState.currentStep() + "/" + taskState.amountSteps();
    }

    private static String getFormattedMessageText(CheckResultDTO result) {
        var messageHeader = result.accepted() ? ReplicEnum.GOOD_WORK : ReplicEnum.BAD_WORK;
        return """
                <b>%s</b>
                
                <b>Правильна відповідь:</b> %s
                <b>Точність:</b> %d%%
                """.formatted(messageHeader.getValue(), result.correctAnswer(), Math.round(result.mark()));
    }
}
