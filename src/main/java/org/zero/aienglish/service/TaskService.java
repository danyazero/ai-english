package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.SentenceHistory;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceHistoryRepository;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.StatusRepository;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.utils.TaskManager;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskManager taskManager;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final SentenceRepository sentenceRepository;
    private final SentenceHistoryRepository sentenceHistoryRepository;

    public SentenceTask getTask(Integer userId) {
        var selectedSentence = sentenceRepository.getSentenceForUser(userId);
        if (selectedSentence.isEmpty()) {
            log.warn("Sentence for task not found for user -> {}", userId);
            throw new RequestException("Sentence for task not found.");
        }



        log.info("Selected words is suitable. Selecting generator randomly.");
        var generatedTask = taskManager.generateTaskWithRandomGenerator(selectedSentence.get());

        saveTaskLog(userId, selectedSentence, generatedTask);

        return generatedTask;
    }

    private void saveTaskLog(Integer userId, Optional<SentenceDTO> selectedSentence, SentenceTask generatedTask) {
        var lastTask = sentenceHistoryRepository.findFirstByUser_IdAndStatus_IdOrderByAtDesc(userId, 1);
        if (lastTask.isPresent()) {
            log.info("This task already logged -> {}", lastTask.get().getTaskType());
            return;
        }
        var userReference = userRepository.getReferenceById(userId);
        var sentenceReference = sentenceRepository.getReferenceById(selectedSentence.get().getId());
        var statusReference = statusRepository.getReferenceById(1);

        var logRow = SentenceHistory.builder()
                .taskType(generatedTask.taskType())
                .sentence(sentenceReference)
                .status(statusReference)
                .user(userReference)
                .at(Instant.now())
                .build();
        sentenceHistoryRepository.save(logRow);
    }

    public CheckResult checkTask(Integer userId, String taskResult) {
        return taskManager.checkResult(userId, taskResult);
    }
}
