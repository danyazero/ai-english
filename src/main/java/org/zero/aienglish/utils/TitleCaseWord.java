package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.VocabularyEntity;

import java.util.function.Function;

@Slf4j
@Component
public class TitleCaseWord implements Function<VocabularyEntity, VocabularyEntity> {
    @Override
    public VocabularyEntity apply(VocabularyEntity word) {
        log.info("Setting into tense word -> {}", word);
        if (!word.getWord().isEmpty()) word.setWord(getTitledSentence(word.getWord()));
        if (!word.getTranslate().isEmpty()) word.setTranslate(getTitledSentence(word.getTranslate()));

        return word;
    }

    private static String getTitledSentence(String word) {
        return Character.toTitleCase(word.charAt(0)) + word.substring(1);
    }
}
