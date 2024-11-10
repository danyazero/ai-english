package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SpeechPartEntity;
import org.zero.aienglish.entity.VocabularyEntity;

import java.util.function.BiFunction;

@Slf4j
@Component
public class SetSpeechPart implements BiFunction<VocabularyEntity, SpeechPartEntity, VocabularyEntity> {
    @Override
    public VocabularyEntity apply(VocabularyEntity vocabulary, SpeechPartEntity speechPart) {
        log.info("Setting speech part {}", speechPart);
        vocabulary.setSpeechPart(speechPart);
        return vocabulary;
    }
}
