package org.zero.aienglish.utils.telegram;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.repository.UserVocabularyRepository;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class VocabularyCommand implements TelegramCommand {
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyMenu vocabularyMenu;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return "/vocabulary";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {

        var chatId = update.getMessage().getChatId();
        var userId = userRepository.findFirstByTelegramId(chatId).getId();
        var vocabulary = userVocabularyRepository.findAllByUser_Id(userId, Pageable.ofSize(5).first());

        var formattedVocabulary = getFormattedVocabulary(vocabulary);
        var pages = vocabulary.getTotalPages();

        var vocabularyMenu = this.vocabularyMenu.apply(vocabulary.getNumber(), pages);

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(formattedVocabulary)
                .parseMode("HTML")
                .replyMarkup(vocabularyMenu)
                .build();

        return List.of(message);
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
