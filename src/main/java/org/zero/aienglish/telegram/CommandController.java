package org.zero.aienglish.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.model.TelegramManager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommandController implements TelegramManager {
    private final Map<String, TelegramCommand> commands;

    public CommandController(List<TelegramCommand> commands) {
        this.commands = commands.stream()
                .collect(Collectors.toMap(
                        TelegramCommand::getName,
                        Function.identity()
                ));
    }

    public List<BotApiMethod<?>> handle(Update update) {
        log.info("Handle command in chat -> {}", update.getMessage().getChatId());
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        log.info("Parsed next command -> {}", command);
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return List.of(new SendMessage(String.valueOf(chatId), "Unknown command: " + command));
        }
    }
}
