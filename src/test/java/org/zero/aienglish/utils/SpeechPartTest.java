package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.SpeechRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeechPartTest {

    @Mock
    private WordMapper wordMapper;
    @Mock
    private SetSpeechPart setSpeechPart;
    @Mock
    private SpeechRepository speechRepository;

    @InjectMocks
    private org.zero.aienglish.utils.SpeechPart speechPartSer;

    private WordDTO currentWord;
    private Vocabulary currentVocabulary;
    private Vocabulary currentVocabularyWith;
    private WordDTO currentIncorrectWord;
    private Vocabulary currentIncorrectVocabulary;
    private Vocabulary currentIncorrectVocabularyUnknown;

    private List<SpeechPart> speechPartList;

    private org.zero.aienglish.entity.SpeechPart speechPartSecond;
    private org.zero.aienglish.entity.SpeechPart speechPart;
    private org.zero.aienglish.entity.SpeechPart unknownSpeechPart;

    @BeforeEach
    void setUp() {
        speechPart = new org.zero.aienglish.entity.SpeechPart(1, "Noun", "test", "test");
        speechPartSecond = new org.zero.aienglish.entity.SpeechPart(2, "Pronoun", "test", "test");
        unknownSpeechPart = new org.zero.aienglish.entity.SpeechPart(3, "Unknown", "test", "test");

        currentWord = new WordDTO("test", (short) 0, "test", "test", "Noun", false);
        currentVocabulary = new Vocabulary(1, "test", "test", null);
        currentVocabularyWith = new Vocabulary(1, "test", "test", speechPart);

        currentIncorrectWord = new WordDTO("test", (short) 0, "test", "test", "Incorrectoun", false);
        currentIncorrectVocabulary = new Vocabulary(2, "test", "test", null);
        currentIncorrectVocabularyUnknown = new Vocabulary(2, "test", "test", unknownSpeechPart);

        speechPartList = new ArrayList<>();

        speechPartList.add(speechPart);
        speechPartList.add(speechPartSecond);
    }

    @Test
    void withSpeechPartTest_WithCorrectData() {

        when(wordMapper.map(currentWord)).thenReturn(currentVocabulary);
        when(setSpeechPart.apply(currentVocabulary, speechPart)).thenReturn(currentVocabularyWith);

        var result = speechPartSer.apply(currentWord, speechPartList);
        System.out.println(result.getSpeechPart().getTitle());

        Assertions.assertEquals(speechPart, result.getSpeechPart());
    }

    @Test
    void withSpeechPartTest_WithIncorrectSpeechPart() {
        when(wordMapper.map(currentIncorrectWord)).thenReturn(currentIncorrectVocabulary);
        when(speechRepository.getReferenceById(1)).thenReturn(unknownSpeechPart);
        when(setSpeechPart.apply(currentIncorrectVocabulary, unknownSpeechPart)).thenReturn(currentIncorrectVocabularyUnknown);

        var result = speechPartSer.apply(currentIncorrectWord, speechPartList);

        Assertions.assertEquals(result.getSpeechPart(), unknownSpeechPart);
    }

    @Test
    void withSpeechPartTest_WithIncorrectSpeechPartWithoutNullObject() {
        when(speechRepository.getReferenceById(1)).thenReturn(null);

        var result = speechPartSer.apply(currentIncorrectWord, speechPartList);

        assertNull(result);
    }


}