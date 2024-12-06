package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.SentenceUserHistory;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskType;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.repository.AnswersHistoryRepository;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.UserRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SentenceCheckTest {
    @Mock
    private AnswersHistoryRepository answersHistoryRepository;
    @Mock
    private SentenceRepository sentenceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccuracyCheck accuracyCheck;
    @Captor
    private ArgumentCaptor<SentenceUserHistory> sentenceUserHistoryArgumentCaptor;

    @InjectMocks
    private SentenceCheck sentenceCheck;

    private TaskResultDTO taskResultDTO;
    private Integer userId;
    private WordResponseDTO word1;
    private WordResponseDTO word2;
    private WordResponseDTO word3;
    private Float mark;

    private User user;
    private Sentence sentence;


    @BeforeEach
    void setUp() {
        mark = 100F;
        word1 = WordResponseDTO.builder()
                .id(1)
                .word("test")
                .speechPart(null)
                .isMarker(false)
                .translate("test translate")
                .build();
        word2 = WordResponseDTO.builder()
                .id(2)
                .word("test2")
                .speechPart(null)
                .isMarker(false)
                .translate("test2 translate")
                .build();
        word3 = WordResponseDTO.builder()
                .id(3)
                .word("test3")
                .speechPart(null)
                .isMarker(false)
                .translate("test3 translate")
                .build();

        taskResultDTO = TaskResultDTO.builder()
                .taskType(TaskType.omittedWord)
                .taskId(2)
                .wordList(List.of(word1, word2, word3))
                .build();
        userId = 5;

        sentence = Sentence.builder()
                .id(2)
                .translation("test")
                .sentence("test test2 test3")
                .build();
        user = User.builder()
                .firstName("test username")
                .lastName("test username")
                .email("test@test.com")
                .role("USER")
                .picture("test picture")
                .id(userId)
                .build();
    }

    @Test
    void sentenceCheckTest() {

        when(sentenceRepository.getReferenceById(taskResultDTO.taskId())).thenReturn(sentence);
        when(accuracyCheck.apply(any(String.class), any(String.class))).thenReturn(mark);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        var res = sentenceCheck.apply(userId, taskResultDTO);
        verify(answersHistoryRepository).save(sentenceUserHistoryArgumentCaptor.capture());

        Assertions.assertEquals(mark.doubleValue(), sentenceUserHistoryArgumentCaptor.getValue().getAccuracy());
        Assertions.assertEquals(mark.doubleValue(), res.mark().doubleValue());
        Assertions.assertTrue(res.accepted());

    }
}