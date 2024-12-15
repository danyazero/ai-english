package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.service.RecomendationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NextVocabularyCallback implements TelegramCallback {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final RecomendationService recomendationService;
    private final UserRepository userRepository;
    private final WordMenu wordMenu;

    @Override
    public String getName() {
        return CallbackEnum.NEXT_VOCABULRAY.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        String callbackId = update.getCallbackQuery().getId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        log.info("Next word for chat -> {}", chatId);
        var userId = userRepository.findFirstByTelegramId(chatId).getId();
        log.info("With chat -> {}, associated user -> {}", chatId, userId);

        var word = recomendationService.nextWord(userId);

        if (word.isEmpty()) {
            var callbackButtons = wordMenu.apply(
                    0,
                    WordMenu.WordMenuState.ENDED
            );

            return List.of(
                    getCallbackProcessed(callbackId),
                    getMessage(callbackButtons, ReplicEnum.WORD_END.getValue(), chatId)
            );
        }

        var messageText = getMessageText(word.get());

        log.info("Prepared word -> {}", messageText);

        var callbackButtons = wordMenu.apply(
                word.get().getId(),
                WordMenu.WordMenuState.DEFAULT
        );

        var callbackProcessed = getCallbackProcessed(callbackId);
        var message = getMessage(callbackButtons, messageText, chatId);

        return List.of(
                callbackProcessed,
                message
        );
    }

    private String getMessageText(VocabularyWordDTO word) {
        return "<b>" + word.getWord() + "</b> - " + word.getTranslate() + "\n\n" + String.join("\n", this.getSentencesForWord(word));
    }

    private List<String> getSentencesForWord(VocabularyWordDTO word) {
        return vocabularySentenceRepository.getAllByVocabulary_Id(word.getId()).stream()
                .map(VocabularySentence::getSentence)
                .map(Sentence::getSentence)
                .limit(3)
                .toList();
    }

    private static SendMessage getMessage(InlineKeyboardMarkup callbackButtons, String messageText, Long chatId) {
        return SendMessage.builder()
                .replyMarkup(callbackButtons)
                .parseMode("HTML")
                .text(messageText)
                .chatId(chatId)
                .build();
    }

    private static AnswerCallbackQuery getCallbackProcessed(String callbackId) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .showAlert(true)
                .build();
    }
}
