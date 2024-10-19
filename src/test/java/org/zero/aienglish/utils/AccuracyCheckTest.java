package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccuracyCheckTest {
    private AccuracyCheck function;
    private String sentence;
    private String similarSentence;
    private String sentenceWithOneAnotherWord;
    private String sentenceWithOneAnotherSymbol;

    @BeforeEach
    void setUp() {
        function = new AccuracyCheck();

        sentence = "They were planning the biggest robbery in history.";
        similarSentence = "They were planning the biggest robbery in history.";
        sentenceWithOneAnotherSymbol = "They were planning the biggest robbery in history,";
        sentenceWithOneAnotherWord = "They were planning the grandest robbery in history.";
    }

    @Test
    @DisplayName("Similar sentences")
    void accuracyCheckTest_SimilarSentence() {
        var result = function.apply(sentence, similarSentence);

        assertEquals(100F, result);
    }

    @Test
    @DisplayName("Sentence with one another symbol")
    void accuracyCheckTest_SentenceWithOneAnotherSymbol() {
        var result = function.apply(sentence, sentenceWithOneAnotherSymbol);

        Assertions.assertTrue(result >= 98F, "Got value %f is greater then 98".formatted(result));
    }

    @Test
    @DisplayName("Sentence with one another word")
    void accuracyCheckTest_SentenceWithOneAnotherWord() {
        var result = function.apply(sentence, sentenceWithOneAnotherWord);

        Assertions.assertTrue(result >= 90F, "Got value %f is greater then 90".formatted(result));
    }

}