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
import org.zero.aienglish.service.TaskService;
import org.zero.aienglish.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExplainCommand implements TelegramCommand {
    private final TaskService taskService;
    private final UserService userService;

    @Override
    public String getName() {
        return "/explain";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var taskExplanation = taskService.getTaskTheoryHelp(serverUserId);

        if (taskExplanation.isEmpty()) {
            log.info("Task explanation is empty");

            var message = SendMessage.builder()
                    .text("Зараз немає активних завдань.")
                    .chatId(chatId)
                    .build();

            return List.of(message);
        }

        var message = SendMessage.builder()
                .text(taskExplanation)
                .parseMode("HTML")
                .chatId(chatId)
                .build();

        return List.of(message);
    }

}
