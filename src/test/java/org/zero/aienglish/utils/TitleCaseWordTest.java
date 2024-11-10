package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.VocabularyEntity;

class TitleCaseWordTest {
    private TitleCaseWord function;
    private VocabularyEntity wordNotTitleCase;
    private VocabularyEntity wordTitleCase;

    @BeforeEach
    void setUp() {
        function = new TitleCaseWord();
        wordNotTitleCase = new VocabularyEntity(1, "test", "test", 0, null);
        wordTitleCase = new VocabularyEntity(1, "Test", "Test", 0, null);
    }

    @Test
    void titleCaseWord_NotTitleCase() {
        var result = function.apply(wordNotTitleCase);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getWord(), wordTitleCase.getWord());
        Assertions.assertEquals(result.getTranslate(), wordTitleCase.getTranslate());

        Assertions.assertEquals(result.getId(), wordNotTitleCase.getId());
        Assertions.assertEquals(result.getViews(), wordNotTitleCase.getViews());
        Assertions.assertEquals(result.getSpeechPart(), wordNotTitleCase.getSpeechPart());
    }

    @Test
    void titleCaseWord_TitleCase() {
        var result = function.apply(wordNotTitleCase);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getWord(), wordTitleCase.getWord());
        Assertions.assertEquals(result.getTranslate(), wordTitleCase.getTranslate());

        Assertions.assertEquals(result.getId(), wordNotTitleCase.getId());
        Assertions.assertEquals(result.getViews(), wordNotTitleCase.getViews());
        Assertions.assertEquals(result.getSpeechPart(), wordNotTitleCase.getSpeechPart());
    }
}