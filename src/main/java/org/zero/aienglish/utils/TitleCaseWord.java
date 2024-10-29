package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Vocabulary;

import java.util.function.Function;

@Slf4j
@Component
public class TitleCaseWord implements Function<Vocabulary, Vocabulary> {
    @Override
    public Vocabulary apply(Vocabulary word) {
        log.info("Setting into title word -> {}", word);
        if (!word.getWord().isEmpty()) word.setWord(getTitledSentence(word.getWord()));
        if (!word.getTranslate().isEmpty()) word.setTranslate(getTitledSentence(word.getTranslate()));

        return word;
    }

    private static String getTitledSentence(String word) {
        return Character.toTitleCase(word.charAt(0)) + word.substring(1);
    }
}
