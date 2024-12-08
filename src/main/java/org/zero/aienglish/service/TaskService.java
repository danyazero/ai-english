package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.utils.TaskManager;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskManager taskManager;
    private final SentenceRepository sentenceRepository;

    public SentenceTask getTask(Integer userId) {
        var selectedSentence = sentenceRepository.getSentenceForUser(userId);
        if (selectedSentence.isEmpty()) {
            log.warn("Sentence for task not found for user -> {}", userId);
            throw new RequestException("Sentence for task not found.");
        }


        log.info("Selected words is suitable. Selecting generator randomly.");
        return taskManager.generateTaskWithRandomGenerator(selectedSentence.get());
    }

    public CheckResult checkTask(Integer userId, TaskResultDTO taskResult) {
        return taskManager.checkResult(userId, taskResult);
    }
}
