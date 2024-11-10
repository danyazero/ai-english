package org.zero.aienglish.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskType;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.TaskManager;
import org.zero.aienglish.utils.TitleCaseWord;
import org.zero.aienglish.utils.SpeechPart;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SentenceServiceTest {
    @Mock
    private TitleCaseWord titleCaseWord;
    @Mock
    AnswersHistoryRepository answersHistoryRepository;
    @Mock
    private SpeechRepository speechRepository;
    @Mock
    private SpeechPart getVocabularySpeechPart;
    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private TaskManager taskManager;

    @InjectMocks
    private SentenceService sentenceService;

    private final String speechPartFirst = "Noun";
    private final String speechPartSecond = "Verb";
    private WordDTO wordFirst;
    private WordDTO wordSecond;
    private SpeechPartEntity speechPartObjectFirst;
    private SpeechPartEntity speechPartObjectSecond;
    private VocabularyEntity vocabularyFirst;
    private VocabularyEntity vocabularySecond;
    private VocabularyEntity vocabularyFirstTitle;
    private VocabularyEntity vocabularySecondTitle;

    private final int userId = 3;
    private final float acceptableAccuracy = 100.0F;
    private SentenceEntity sentence;
    private UserEntity user;
    private SentenceUserHistoryEntity history;
    private final String correctSentence = "test test_1";
    private TaskResultDTO taskResult;
    private WordResponseDTO word_1;
    private WordResponseDTO word_2;

    @BeforeEach
    void setUp() {
        wordFirst = new WordDTO("Test", (short) 0, "Test", "test_translated", speechPartFirst, false);
        wordSecond = new WordDTO("Test_1", (short) 1, "Test", "test_translated_1", speechPartSecond, false);

        speechPartObjectFirst = new SpeechPartEntity(1, speechPartFirst, speechPartFirst, speechPartFirst);
        speechPartObjectSecond = new SpeechPartEntity(2, speechPartSecond, speechPartSecond, speechPartSecond);

        vocabularyFirst = new VocabularyEntity(null, "test", "test_translated", null, speechPartObjectFirst);
        vocabularySecond = new VocabularyEntity(null, "test_1", "test_translated_1", null, speechPartObjectSecond);

        vocabularyFirstTitle = new VocabularyEntity(null, "Test", "Test_translated", null, speechPartObjectFirst);
        vocabularySecondTitle = new VocabularyEntity(null, "Test_1", "Test_translated_1", null, speechPartObjectSecond);

        user = UserEntity.builder()
                .id(userId)
                .username("danyazero")
                .build();

        word_1 = new WordResponseDTO(1, "test", "test_translated", speechPartObjectFirst, false);
        word_2 = new WordResponseDTO(2, "test_1", "test_translated_1", speechPartObjectSecond, false);
        List<WordResponseDTO> wordList = List.of(word_1, word_2);
        taskResult = new TaskResultDTO(1, TaskType.omittedWord, wordList);

        sentence = SentenceEntity.builder()
                .id(1)
                .views(null)
                .theme(null)
                .sentence("test test_1")
                .translation(correctSentence)
                .build();
        history = SentenceUserHistoryEntity.builder()
                .user(user)
                .sentence(sentence)
                .lastAnswered(Instant.now())
                .accuracy((double) acceptableAccuracy)
                .build();
    }

    @Test
    void addWordList_Success() {
        var wordList = List.of(wordFirst, wordSecond);
        List<SpeechPartEntity> speechPartObjectList = List.of(speechPartObjectFirst, speechPartObjectSecond);

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
        List<SpeechPartEntity> speechPartObjectList = List.of(speechPartObjectFirst, speechPartObjectSecond);

        var speechPartList = Arrays.asList(speechPartFirst, speechPartSecond);
        String[] speechPartArray = speechPartList.toArray(new String[0]);
        List<VocabularyEntity> alreadySavedList = List.of(vocabularySecond);


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
        List<SpeechPartEntity> speechPartObjectList = List.of(speechPartObjectFirst);

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