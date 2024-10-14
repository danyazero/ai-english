package org.zero.aienglish.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.SpeechRepository;
import org.zero.aienglish.repository.VocabularyRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.utils.GetVocabularyWithSpeechPart;
import org.zero.aienglish.utils.SetSpeechPart;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SentenceServiceTest {
    @Mock private WordMapper wordMapper;
    @Mock private SetSpeechPart setSpeechPart;
    @Mock private SpeechRepository speechRepository;
    @Mock private VocabularyRepository vocabularyRepository;
    @Mock private VocabularySentenceRepository vocabularySentenceRepository;
    @Mock GetVocabularyWithSpeechPart getVocabularyWithSpeechPart;

    @InjectMocks
    private SentenceService sentenceService;

    private final String speechPartFirst = "Noun";
    private final String speechPartSecond = "Verb";
    private WordDTO wordFirst;
    private WordDTO wordSecond;
    private SpeechPart speechPartObjectFirst;
    private SpeechPart speechPartObjectSecond;
    private Vocabulary mappedWordFirst;
    private Vocabulary mappedWordSecond;

    @BeforeEach
    void setUp() {
        wordFirst = new WordDTO("test", "test_translated", speechPartFirst);
        wordSecond = new WordDTO("test_1", "test_translated_1", speechPartSecond);

        speechPartObjectFirst = new SpeechPart(1, speechPartFirst, speechPartFirst, speechPartFirst);
        speechPartObjectSecond = new SpeechPart(2, speechPartSecond, speechPartSecond, speechPartSecond);
        mappedWordFirst = new Vocabulary(null, "test", "test_translated", null, speechPartObjectFirst);
        mappedWordSecond = new Vocabulary(null, "test_1", "test_translated_1", null, speechPartObjectSecond);
    }

    @Test
    void addWordList_Success() {
        var wordList = List.of(wordFirst, wordSecond);
        List<SpeechPart> speechPartObjectList = List.of(speechPartObjectFirst, speechPartObjectSecond);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(speechPartObjectList);
        when(getVocabularyWithSpeechPart.apply(wordFirst, speechPartObjectList)).thenReturn(mappedWordFirst);
        when(getVocabularyWithSpeechPart.apply(wordSecond, speechPartObjectList)).thenReturn(mappedWordSecond);

        var result = sentenceService.getWordList(wordList);

        Assertions.assertEquals(result.getFirst().getSpeechPart().getId(), speechPartObjectFirst.getId());
        Assertions.assertEquals(result.getFirst().getSpeechPart().getTitle(), speechPartObjectFirst.getTitle());
        Assertions.assertEquals(result.get(1).getSpeechPart().getId(), speechPartObjectSecond.getId());
        Assertions.assertEquals(wordList.size(), result.size());
    }

    @Test
    void addWordList_SuccessWithInvalidSpeechPart() {
        var wordList = List.of(wordFirst, wordSecond);
        List<SpeechPart> speechPartObjectList = List.of(speechPartObjectFirst);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(speechPartObjectList);
        when(getVocabularyWithSpeechPart.apply(wordFirst, speechPartObjectList)).thenReturn(mappedWordFirst);
        when(getVocabularyWithSpeechPart.apply(wordSecond, speechPartObjectList)).thenReturn(null);

        var result = sentenceService.getWordList(wordList);

        Assertions.assertEquals(result.getFirst().getSpeechPart().getId(), speechPartObjectFirst.getId());
        Assertions.assertEquals(result.getFirst().getSpeechPart().getTitle(), speechPartObjectFirst.getTitle());
        Assertions.assertNotEquals(wordList.size(), result.size());
    }

    @Test
    void addWordList_NotFoundInDatabase() {
        var wordList = List.of(wordFirst, wordSecond);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(List.of());

        Assertions.assertThrows(RequestException.class, () -> sentenceService.getWordList(wordList));
    }
}