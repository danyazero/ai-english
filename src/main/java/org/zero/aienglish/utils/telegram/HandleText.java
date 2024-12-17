package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.CheckResult;
import org.zero.aienglish.model.ReplicEnum;
import org.zero.aienglish.model.TelegramCallbackButton;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.service.TaskService;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleText implements Function<Update, List<BotApiMethod<?>>> {
    private final UserRepository userRepository;
    private final InlineKeyboard inlineKeyboard;
    private final TaskService taskService;

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getMessage().getChatId();
        log.info("Getted text in chat -> {}", chatId);
        var userId = userRepository.findFirstByTelegramId(chatId).getId();
        log.info("Chat -> {}, associated with user -> {}", chatId, userId);
        var gettedMessageText = update.getMessage().getText();
        log.info("Getted text in reply -> {}", gettedMessageText);

        var checkResult = taskService.checkTask(userId, gettedMessageText);
        log.info("Check result -> {}", checkResult.mark());

        var messageText = getFormattedMessageText(checkResult);
        var keyboardMarkup = inlineKeyboard.apply(
                List.of(
                        new TelegramCallbackButton(CallbackEnum.NEXT_TASK.name(), ReplicEnum.NEXT.getValue())
                )
        );

        var message = SendMessage.builder()
                .text(messageText)
                .chatId(chatId)
                .parseMode("HTML")
                .replyMarkup(keyboardMarkup)
                .build();

        return List.of(message);
    }

    private static String getFormattedMessageText(CheckResult result) {
        var messageHeader = result.accepted() ? ReplicEnum.GOOD_WORK.getValue() : ReplicEnum.BAD_WORK.getValue();
        return  """
                <b>%s</b>
                
                <b>Правильна відповідь:</b> %s
                <b>Точність:</b> %d%%
                """.formatted(messageHeader, result.correctAnswer(), Math.round(result.mark()));
    }
}
