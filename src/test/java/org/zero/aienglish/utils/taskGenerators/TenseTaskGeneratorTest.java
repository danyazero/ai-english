package org.zero.aienglish.utils.taskGenerators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskType;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.repository.AnswersHistoryRepository;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.SentenceTenseRepository;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.utils.AccuracyCheck;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenseTaskGeneratorTest {
    @Mock
    private SentenceRepository sentenceRepository;
    @Mock
    private SentenceTenseRepository sentenceTenseRepository;
    @Mock
    private AccuracyCheck accuracyCheck;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnswersHistoryRepository sentenceUserHistory;
    @Captor
    private ArgumentCaptor<SentenceUserHistoryEntity> sentenceUserHistoryArgumentCaptor;

    @InjectMocks
    private TenseTaskGenerator tenseTaskGenerator;

    private Integer userId;
    private TaskResultDTO taskResultDTO;
    private WordResponseDTO word1;
    private Float mark;

    private UserEntity user;
    private SentenceEntity sentence;
    private TenseEntity tense;

    @BeforeEach
    void setUp() {
        mark = 100F;

        var time = TimeEntity.builder()
                .id(2)
                .title("Present")
                .build();
        var duration = DurationEntity.builder()
                .id(5)
                .title("Simple")
                .build();
        tense = TenseEntity.builder()
                .titleTime(time)
                .titleDuration(duration)
                .verb("test")
                .formula("test")
                .build();
        word1 = WordResponseDTO.builder()
                .id(null)
                .word("Present Simple")
                .speechPart(null)
                .isMarker(false)
                .translate("__")
                .build();

        taskResultDTO = TaskResultDTO.builder()
                .taskType(TaskType.omittedWord)
                .taskId(2)
                .wordList(List.of(word1))
                .build();
        userId = 5;

        sentence = SentenceEntity.builder()
                .id(2)
                .translation("test")
                .sentence("test test2 test3")
                .build();
        user = UserEntity.builder()
                .username("test username")
                .id(userId)
                .build();
    }

    @Test
    void checkTask() {
        when(sentenceRepository.findById(taskResultDTO.taskId())).thenReturn(Optional.of(sentence));
        when(sentenceTenseRepository.findAllBySentence(sentence)).thenReturn(List.of(tense));
        when(accuracyCheck.apply(anyString(), anyString())).thenReturn(mark);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        var res = tenseTaskGenerator.checkTask(userId, taskResultDTO);
        verify(sentenceUserHistory).save(sentenceUserHistoryArgumentCaptor.capture());

        Assertions.assertEquals(mark.doubleValue(), sentenceUserHistoryArgumentCaptor.getValue().getAccuracy());
        Assertions.assertEquals(mark.doubleValue(), res.mark().doubleValue());
        Assertions.assertTrue(res.accepted());
    }

    @Test
    @DisplayName("Check Task with incorrect taskId")
    void checkTask_2() {
        when(sentenceRepository.findById(taskResultDTO.taskId())).thenReturn(Optional.empty());
        Assertions.assertThrows(RequestException.class, () -> tenseTaskGenerator.checkTask(userId, taskResultDTO));
    }
}