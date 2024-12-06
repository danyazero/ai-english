package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;

import java.util.function.BiFunction;

@Slf4j
@Component
public class SetSpeechPart implements BiFunction<Vocabulary, SpeechPart, Vocabulary> {
    @Override
    public Vocabulary apply(Vocabulary vocabulary, SpeechPart speechPart) {
        log.info("Setting speech part {}", speechPart);
        vocabulary.setSpeechPart(speechPart);
        return vocabulary;
    }
}
