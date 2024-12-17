package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.model.CallbackEnum;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.repository.UserVocabularyRepository;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class VocabularyPageCallback implements TelegramCallback {
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyMenu vocabularyMenu;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return CallbackEnum.VOCABULARY_PAGE.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var splitedCallback = update.getCallbackQuery().getData().split(" ");

        if (splitedCallback.length < 1) {
            log.warn("Incorrect vocabulary page, here callback -> {}", update.getCallbackQuery().getData());
            return List.of();
        }

        var parsedPage = Integer.parseInt(splitedCallback[1]);
        var userId = userRepository.findFirstByTelegramId(chatId).getId();
        var vocabulary = userVocabularyRepository.findAllByUser_Id(userId, Pageable.ofSize(5).withPage(parsedPage));



        var updatedMessageText = getFormattedVocabulary(vocabulary);
        var updatedMessage = EditMessageText.builder()
                .text(updatedMessageText)
                .messageId(messageId)
                .parseMode("HTML")
                .chatId(chatId)
                .build();

        var updatedMenu = vocabularyMenu.apply(parsedPage, vocabulary.getTotalPages());
        var editKeyboardMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(updatedMenu)
                .messageId(messageId)
                .chatId(chatId)
                .build();

        return List.of(
                updatedMessage,
                editKeyboardMarkup
        );
    }

    private static String getFormattedVocabulary(Page<UserVocabulary> vocabulary) {
        var wordList = vocabulary.get()
                .map(getFormatedWord())
                .toList();
        return String.join("\n", wordList) + "\n\n" + vocabulary.getNumber() + "/" + (vocabulary.getTotalPages() - 1);
    }

    @NotNull
    private static Function<UserVocabulary, String> getFormatedWord() {
        return element -> "<b>" + element.getWord().getWord() + "</b> - " + element.getWord().getTranslate();
    }

}
