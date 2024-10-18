package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Vocabulary;

import java.util.function.Function;

@Component
public class TitleCaseWord implements Function<Vocabulary, Vocabulary> {
    @Override
    public Vocabulary apply(Vocabulary word) {
        word.setWord(getTitledSentence(word.getWord()));
        word.setTranslate(getTitledSentence(word.getTranslate()));
        return word;
    }

    private static String getTitledSentence(String word) {
        return Character.toTitleCase(word.charAt(0)) + word.substring(1);
    }
}
