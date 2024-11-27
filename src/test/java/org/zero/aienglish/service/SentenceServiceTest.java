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
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.*;
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
    private SpeechRepository speechRepository;
    @Mock
    private SpeechPart getVocabularySpeechPart;
    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private VocabularySentenceRepository vocabularySentenceRepository;
    @Mock
    private SentenceTenseRepository sentenceTenseRepository;
    @Mock
    private WordMapper wordMapper;

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
    private WordResponseDTO word1;
    private WordResponseDTO word_1;
    private WordResponseDTO word_2;

    private SentenceDTO sentenceDTO;
    private SentenceTenseEntity sentenceTense;
    private VocabularySentenceEntity vocabularySentence;
    private VocabularyEntity vocabulary;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        sentenceDTO = new SentenceDTO() {
            @Override
            public Integer getId() {
                return 2;
            }

            @Override
            public String getTranslate() {
                return "test translate";
            }

            @Override
            public String getSentence() {
                return "test sentence";
            }

            @Override
            public Double getScore() {
                return 98.0;
            }
        };
        wordFirst = new WordDTO("Test", (short) 0, "Test", "test_translated", speechPartFirst, false);
        wordSecond = new WordDTO("Test_1", (short) 1, "Test", "test_translated_1", speechPartSecond, false);

        speechPartObjectFirst = new SpeechPartEntity(1, speechPartFirst, speechPartFirst, speechPartFirst);
        speechPartObjectSecond = new SpeechPartEntity(2, speechPartSecond, speechPartSecond, speechPartSecond);

        vocabularyFirst = new VocabularyEntity(null, "test", "test_translated", speechPartObjectFirst);
        vocabularySecond = new VocabularyEntity(null, "test_1", "test_translated_1", speechPartObjectSecond);

        vocabularyFirstTitle = new VocabularyEntity(null, "Test", "Test_translated", speechPartObjectFirst);
        vocabularySecondTitle = new VocabularyEntity(null, "Test_1", "Test_translated_1", speechPartObjectSecond);

        user = UserEntity.builder()
                .id(userId)
                .firstName("danyazero")
                .lastName("danyazero")
                .email("danyazero@gmail.com")
                .role("USER")
                .picture("picture")
                .build();

        word_1 = new WordResponseDTO(1, "test", "test_translated", speechPartObjectFirst.getTitle(), false);
        word_2 = new WordResponseDTO(2, "test_1", "test_translated_1", speechPartObjectSecond.getTitle(), false);
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

        var time = TimeEntity.builder()
                .id(2)
                .title("Present")
                .build();

        var duration = DurationEntity.builder()
                .id(5)
                .title("Simple")
                .build();

        var tense = TenseEntity.builder()
                .titleTime(time)
                .titleDuration(duration)
                .verb("test")
                .formula("test")
                .build();

        sentenceTense = SentenceTenseEntity.builder()
                .sentence(sentence)
                .tense(tense)
                .build();

        vocabularySentence = VocabularySentenceEntity.builder()
                .order((short) 0)
                .sentence(sentence)
                .vocabulary(vocabulary)
                .isMarker(false)
                .defaultWord("test")
                .build();

        word1 = WordResponseDTO.builder()
                .word("test")
                .translate("test translate")
                .build();
        taskDTO = TaskDTO.builder()
                .id(2)
                .words(List.of(word1))
                .tenses(List.of(tense))
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

    @Test
    void sentenceDetailsExtractor() {
        when(sentenceTenseRepository.findAllBySentenceId(sentenceDTO.getId())).thenReturn(List.of(sentenceTense));
        when(vocabularySentenceRepository.getAllBySentenceIdOrderByOrder(sentenceDTO.getId())).thenReturn(List.of(vocabularySentence));
        when(wordMapper.map(vocabularySentence)).thenReturn(word1);
        var res = sentenceService.getSentenceDetails(sentenceDTO);

        Assertions.assertEquals(taskDTO.id(), res.id());
        Assertions.assertEquals(taskDTO.words(), res.words());
        Assertions.assertEquals(taskDTO.tenses(), res.tenses());
        Assertions.assertEquals(taskDTO.sentence(), res.sentence());
    }
}