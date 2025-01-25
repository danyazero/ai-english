package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.CheckResult;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.repository.SentenceRepository;

import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceCheck implements BiFunction<Integer, TaskResultDTO, CheckResult> {
    private final SentenceRepository sentenceRepository;
    private final AccuracyCheck accuracyCheck;

    @Override
    public CheckResult apply(Integer userId, TaskResultDTO taskResult) {
        var currentSentence = sentenceRepository.findById(taskResult.taskId());
        if (currentSentence.isEmpty()) throw new RequestException("Current sentence not found");

        var founded = sentenceRepository.getReferenceById(currentSentence.get().getId());
        log.info("Founded words -> {}", founded.getSentence());
        var correctSentence = founded.getSentence().replaceAll("[,?.`\\-*]", "");
        Float answerMark = accuracyCheck.apply(taskResult.answer(), correctSentence);

        return CheckResult.builder()
                .mark(answerMark)
                .accepted(answerMark >= 98F)
                .build();
    }
}
