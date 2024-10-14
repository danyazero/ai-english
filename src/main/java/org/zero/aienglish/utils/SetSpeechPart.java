package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;

import java.util.function.BiFunction;

@Component
public class SetSpeechPart implements BiFunction<Vocabulary, SpeechPart, Vocabulary> {
    @Override
    public Vocabulary apply(Vocabulary vocabulary, SpeechPart speechPart) {
        vocabulary.setSpeechPart(speechPart);
        return vocabulary;
    }
}
