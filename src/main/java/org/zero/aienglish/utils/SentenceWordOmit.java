package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.model.TaskWord;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceWordOmit implements BiFunction<String, List<TaskWord>, String> {
    private final AccuracyCheck accuracyCheck;
    
    @Override
    public String apply(String sentence, List<TaskWord> omittedWords) {
        log.info("Word for ommit -> {}", omittedWords.stream().map(TaskWord::defaultWord).toList());
        var sentenceParts = sentence.split(" ");

        for (int i = 0; i < sentenceParts.length; i++) {
            final String currentWord = sentenceParts[i];
            var founded = findWord(omittedWords, currentWord, i);

            if (founded.isPresent()) {
                log.info("Replacing word -> {}, with order {} in position {}", sentenceParts[i], founded.get().order(), i);
                sentenceParts[i] = sentenceParts[i].replaceAll("[A-Za-z']+", "__");
                if (sentenceParts[i].contains("*")) {
                    log.info("Adding task key -> {}", founded.get().defaultWord());
                    sentenceParts[i] = sentenceParts[i].replace("*", " (" + founded.get().defaultWord().toLowerCase() + ")");
                }
            }
        }

        return String.join(" ", sentenceParts);
    }

    private Optional<TaskWord> findWord(List<TaskWord> omittedWords, String currentWord, Integer currentPosition) {
        return omittedWords.stream()
                .filter(elem -> isCurrentWord(currentWord, currentPosition, elem))
                .findFirst();
    }

    private boolean isCurrentWord(String currentWord, Integer currentPosition, TaskWord elem) {
        String cleanedWord = currentWord.replaceAll("[,.\\-!?*]", "");

        return accuracyCheck.apply(elem.word(), cleanedWord) > 80F
                && Objects.equals(elem.order(), currentPosition);
    }
}
