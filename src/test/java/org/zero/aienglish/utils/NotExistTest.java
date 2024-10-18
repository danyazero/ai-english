package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.WordDTO;

import java.util.ArrayList;
import java.util.List;

class NotExistTest {
    private NotExist function;
    private WordDTO savedWordNoun;
    private WordDTO savedWordVerb;
    private WordDTO notSavedWord;
    private List<Vocabulary> vocabularyList;

    @BeforeEach
    void setUp() {
        function = new NotExist();

        savedWordNoun = new WordDTO("test", "test", "Noun");
        savedWordVerb = new WordDTO("test", "test", "Verb");
        notSavedWord = new WordDTO("test_2", "test_2", "Noun");

        vocabularyList = new ArrayList<>();

        var speechPartNoun = new SpeechPart(1, "Noun", "test", "test");

        vocabularyList.add(new Vocabulary(1, "test", "test", 0, speechPartNoun));
        vocabularyList.add(new Vocabulary(2, "test2", "test2", 0, speechPartNoun));
        vocabularyList.add(new Vocabulary(3, "test3", "test3", 0, speechPartNoun));
    }

    @Test
    void alreadySaved_WordExistTest() {
        var result = function.apply(vocabularyList, savedWordNoun);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result);

        var firstVoc = vocabularyList.get(0);
        var secondVoc = vocabularyList.get(1);
        var thirdVoc = vocabularyList.get(2);

        Assertions.assertTrue(firstVoc.getWord().equalsIgnoreCase(savedWordNoun.getWord()) && firstVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordNoun.getSpeechPart()));
        Assertions.assertFalse(secondVoc.getWord().equalsIgnoreCase(savedWordNoun.getWord()) && secondVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordNoun.getSpeechPart()));
        Assertions.assertFalse(thirdVoc.getWord().equalsIgnoreCase(savedWordNoun.getWord()) && thirdVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordNoun.getSpeechPart()));
    }

    @Test
    void alreadySaved_WordNotExistWithSameSpeechPartTest() {
        var result = function.apply(vocabularyList, savedWordVerb);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result);

        var firstVoc = vocabularyList.get(0);
        var secondVoc = vocabularyList.get(1);
        var thirdVoc = vocabularyList.get(2);

        Assertions.assertFalse(firstVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && firstVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
        Assertions.assertFalse(secondVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && secondVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
        Assertions.assertFalse(thirdVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && thirdVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
    }

    @Test
    void alreadySaved_WordNotExistTest() {
        var result = function.apply(vocabularyList, notSavedWord);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result);

        var firstVoc = vocabularyList.get(0);
        var secondVoc = vocabularyList.get(1);
        var thirdVoc = vocabularyList.get(2);

        Assertions.assertFalse(firstVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && firstVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
        Assertions.assertFalse(secondVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && secondVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
        Assertions.assertFalse(thirdVoc.getWord().equalsIgnoreCase(savedWordVerb.getWord()) && thirdVoc.getSpeechPart().getTitle().equalsIgnoreCase(savedWordVerb.getSpeechPart()));
    }
}