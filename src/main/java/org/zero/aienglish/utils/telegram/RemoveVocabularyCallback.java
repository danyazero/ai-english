package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.service.RecomendationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveVocabularyCallback implements TelegramCallback {
    private final RecomendationService recomendationService;
    private final UserRepository userRepository;
    private final WordMenu wordMenu;

    @Override
    public String getName() {
        return CallbackEnum.REMOVE.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        log.info("Save word for chat -> {}", chatId);
        var userId = userRepository.findFirstByTelegramId(chatId).getId();
        log.info("With chat -> {}, associated user -> {}", chatId, userId);
        var callbackSplited = update.getCallbackQuery().getData().split(" ");

        if (callbackSplited.length < 1) {
            log.warn(
                    "Error occurred while removing word in chat -> {}, with callback -> {}",
                    chatId, update.getCallbackQuery().getData()
            );
        }
        var wordForRemoveId = Integer.parseInt(callbackSplited[1]);
        recomendationService.removeWord(userId, wordForRemoveId);

        var updatedMenu = wordMenu.apply(
                wordForRemoveId,
                WordMenu.WordMenuState.DEFAULT
        );

        var editKeyboardMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(updatedMenu)
                .messageId(messageId)
                .chatId(chatId)
                .build();

        return List.of(editKeyboardMarkup);
    }
}
