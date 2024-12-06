package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;

class SetSpeechPartTest {
    private SetSpeechPart setSpeechPart;

    @BeforeEach
    void setUp() {
        setSpeechPart = new SetSpeechPart();
    }

    @Test
    @DisplayName("Setting SpeechPart on Vocabulary object")
    void apply() {
        var speechPart = new SpeechPart(1, "test", "test", "test");
        var vocabulary = new Vocabulary(1, "test", "test", null);

        Assertions.assertNull(vocabulary.getSpeechPart());

        var newVocabulary = setSpeechPart.apply(vocabulary, speechPart);

        Assertions.assertNotNull(newVocabulary.getSpeechPart());
        Assertions.assertEquals(speechPart, newVocabulary.getSpeechPart());
    }
}