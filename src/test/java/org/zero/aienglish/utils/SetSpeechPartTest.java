package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.SpeechPartEntity;
import org.zero.aienglish.entity.VocabularyEntity;

class SetSpeechPartTest {
    private SetSpeechPart setSpeechPart;

    @BeforeEach
    void setUp() {
        setSpeechPart = new SetSpeechPart();
    }

    @Test
    @DisplayName("Setting SpeechPart on Vocabulary object")
    void apply() {
        var speechPart = new SpeechPartEntity(1, "test", "test", "test");
        var vocabulary = new VocabularyEntity(1, "test", "test", null);

        Assertions.assertNull(vocabulary.getSpeechPart());

        var newVocabulary = setSpeechPart.apply(vocabulary, speechPart);

        Assertions.assertNotNull(newVocabulary.getSpeechPart());
        Assertions.assertEquals(speechPart, newVocabulary.getSpeechPart());
    }
}