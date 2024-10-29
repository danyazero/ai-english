package org.zero.aienglish.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.SpeechRepository;
import org.zero.aienglish.repository.VocabularyRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.utils.TitleCaseWord;
import org.zero.aienglish.utils.SpeechPart;
import org.zero.aienglish.utils.SetSpeechPart;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SentenceServiceTest {
    @Mock
    private TitleCaseWord titleCaseWord;
    @Mock
    private WordMapper wordMapper;
    @Mock
    private SetSpeechPart setSpeechPart;
    @Mock
    private SpeechRepository speechRepository;
    @Mock
    private SpeechPart getVocabularySpeechPart;
    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private VocabularySentenceRepository vocabularySentenceRepository;

    @InjectMocks
    private SentenceService sentenceService;

    private final String speechPartFirst = "Noun";
    private final String speechPartSecond = "Verb";
    private WordDTO wordFirst;
    private WordDTO wordSecond;
    private org.zero.aienglish.entity.SpeechPart speechPartObjectFirst;
    private org.zero.aienglish.entity.SpeechPart speechPartObjectSecond;
    private Vocabulary vocabularyFirst;
    private Vocabulary vocabularySecond;
    private Vocabulary vocabularyFirstTitle;
    private Vocabulary vocabularySecondTitle;

    @BeforeEach
    void setUp() {
        wordFirst = new WordDTO("Test", (short) 0, "Test", "test_translated", speechPartFirst, false);
        wordSecond = new WordDTO("Test_1", (short) 1, "Test", "test_translated_1", speechPartSecond, false);

        speechPartObjectFirst = new org.zero.aienglish.entity.SpeechPart(1, speechPartFirst, speechPartFirst, speechPartFirst);
        speechPartObjectSecond = new org.zero.aienglish.entity.SpeechPart(2, speechPartSecond, speechPartSecond, speechPartSecond);

        vocabularyFirst = new Vocabulary(null, "test", "test_translated", null, speechPartObjectFirst);
        vocabularySecond = new Vocabulary(null, "test_1", "test_translated_1", null, speechPartObjectSecond);

        vocabularyFirstTitle = new Vocabulary(null, "Test", "Test_translated", null, speechPartObjectFirst);
        vocabularySecondTitle = new Vocabulary(null, "Test_1", "Test_translated_1", null, speechPartObjectSecond);
    }

    @Test
    void addWordList_Success() {
        var wordList = List.of(wordFirst, wordSecond);
        List<org.zero.aienglish.entity.SpeechPart> speechPartObjectList = List.of(speechPartObjectFirst, speechPartObjectSecond);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(speechPartObjectList);
        when(vocabularyRepository.findAllByWord(wordList.stream().map(WordDTO::getDefaultWord).toArray(String[]::new))).thenReturn(List.of());

        when(titleCaseWord.apply(vocabularyFirst)).thenReturn(vocabularyFirstTitle);
        when(titleCaseWord.apply(vocabularySecond)).thenReturn(vocabularySecondTitle);

        when(getVocabularySpeechPart.apply(wordFirst, speechPartObjectList)).thenReturn(vocabularyFirst);
        when(getVocabularySpeechPart.apply(wordSecond, speechPartObjectList)).thenReturn(vocabularySecond);

        var result = sentenceService.getVocabularyList(wordList);

        Assertions.assertEquals(wordList.size(), result.size());
        Assertions.assertEquals(result.getFirst().getSpeechPart().getId(), speechPartObjectFirst.getId());
        Assertions.assertEquals(result.getFirst().getSpeechPart().getTitle(), speechPartObjectFirst.getTitle());
        Assertions.assertEquals(result.get(1).getSpeechPart().getId(), speechPartObjectSecond.getId());
    }

    @Test
    void addWordList_WithAlreadyExist() {
        var wordList = List.of(wordFirst, wordSecond);
        List<org.zero.aienglish.entity.SpeechPart> speechPartObjectList = List.of(speechPartObjectFirst, speechPartObjectSecond);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);
        List<Vocabulary> alreadySavedList = List.of(vocabularySecond);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(speechPartObjectList);
        when(vocabularyRepository.findAllByWord(wordList.stream().map(WordDTO::getDefaultWord).toArray(String[]::new))).thenReturn(alreadySavedList);
        when(titleCaseWord.apply(vocabularyFirst)).thenReturn(vocabularyFirstTitle);
        when(getVocabularySpeechPart.apply(wordFirst, speechPartObjectList)).thenReturn(vocabularyFirst);

        var result = sentenceService.getVocabularyList(wordList);

        Assertions.assertEquals(result.getFirst().getSpeechPart().getId(), speechPartObjectFirst.getId());
        Assertions.assertEquals(result.getFirst().getSpeechPart().getTitle(), speechPartObjectFirst.getTitle());
        Assertions.assertEquals(wordList.size() - 1, result.size());
    }


    @Test
    void addWordList_SuccessWithInvalidSpeechPart() {
        var wordList = List.of(wordFirst, wordSecond);
        List<org.zero.aienglish.entity.SpeechPart> speechPartObjectList = List.of(speechPartObjectFirst);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);


        when(speechRepository.findByTitle(speechPartArray)).thenReturn(speechPartObjectList);
        when(vocabularyRepository.findAllByWord(wordList.stream().map(WordDTO::getDefaultWord).toArray(String[]::new))).thenReturn(List.of());

        when(titleCaseWord.apply(vocabularyFirst)).thenReturn(vocabularyFirstTitle);
        when(getVocabularySpeechPart.apply(wordFirst, speechPartObjectList)).thenReturn(vocabularyFirst);
        when(getVocabularySpeechPart.apply(wordSecond, speechPartObjectList)).thenReturn(null);

        var result = sentenceService.getVocabularyList(wordList);

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

        Assertions.assertThrows(RequestException.class, () -> sentenceService.getVocabularyList(wordList));
    }
}