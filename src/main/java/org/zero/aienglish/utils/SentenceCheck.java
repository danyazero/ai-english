package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SentenceHistory;
import org.zero.aienglish.model.CheckResult;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.repository.SentenceHistoryRepository;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.UserRepository;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceCheck implements BiFunction<Integer, TaskResultDTO, CheckResult> {
    private final SentenceHistoryRepository answersHistoryRepository;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;
    private final AccuracyCheck accuracyCheck;

    @Override
    public CheckResult apply(Integer userId, TaskResultDTO taskResult) {
        var founded = sentenceRepository.getReferenceById(taskResult.taskId());
        log.info("Founded words -> {}", founded.getSentence());
        var resultSentence = taskResult.wordList().stream()
                .map(WordResponseDTO::getWord)
                .collect(Collectors.joining(" ")).replaceAll("[,?.`\\\\-]", "");
        log.info("User response words -> {}", resultSentence);
        var correctSentence = founded.getSentence().replaceAll("[,?.`\\-]", "");
        Float answerMark = accuracyCheck.apply(resultSentence, correctSentence);

        var user = userRepository.getReferenceById(userId);
        var answerHistory = SentenceHistory.builder()
                .user(user)
                .sentence(founded)
                .at(Instant.now())
                .build();
        answersHistoryRepository.save(answerHistory);

        return CheckResult.builder()
                .mark(answerMark)
                .accepted(answerMark >= 98F)
                .build();
    }
}
