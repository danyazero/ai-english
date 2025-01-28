package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.model.TaskWord;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormatSentence implements BiFunction<String, ImprovedList<TaskWord>, String> {

    @Override
    public String apply(String sentence, ImprovedList<TaskWord> omittedWords) {
        var sentenceParts = sentence.split(" ");

        for (int i = 0; i < sentenceParts.length; i++) {
            var isCurrentWordPredicate = getIsCurrentWordPredicate(i);
            var founded = omittedWords.find(isCurrentWordPredicate);

            if (founded.isPresent()) {
                log.info("Replacing word -> {}, with order {} in position {}", sentenceParts[i], founded.get().order(), i);
                sentenceParts[i] = sentenceParts[i].replaceAll("[A-Za-z']+", "__");

                sentenceParts[i] = replaceWithKey(sentenceParts[i], founded.get())
                        .orElse(sentenceParts[i]);
            }
        }

        return String.join(" ", sentenceParts);
    }

    private static Optional<String> replaceWithKey(String sentencePart, TaskWord word) {
        if (sentencePart.contains("*")) {
            var replacedWithKey = sentencePart.replaceAll("[*]", " (" + word.defaultWord().toLowerCase() + ")");

            return Optional.of(replacedWithKey);
        }

        return Optional.empty();
    }

    private static Predicate<TaskWord> getIsCurrentWordPredicate(int i) {
        return element -> element.order().equals(i);
    }
}
