package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SentenceHistory;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskManager {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final SentenceHistoryRepository sentenceHistoryRepository;
    private final SentenceRepository sentenceRepository;
    private final Map<TaskType, TaskGenerator> taskMap;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public TaskManager(
            List<TaskGenerator> taskList,
            VocabularySentenceRepository vocabularySentenceRepository,
            SentenceHistoryRepository sentenceHistoryRepository,
            SentenceRepository sentenceRepository,
            StatusRepository statusRepository,
            UserRepository userRepository
    ) {
        taskMap = taskList.stream().collect(Collectors.toMap(TaskGenerator::getTaskName, Function.identity()));
        this.vocabularySentenceRepository = vocabularySentenceRepository;
        this.sentenceHistoryRepository = sentenceHistoryRepository;
        this.sentenceRepository = sentenceRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    public SentenceTask generateTask(TaskType taskType, SentenceDTO selectedSentence) {
        return taskMap.get(taskType).generateTask(selectedSentence);
    }

    public SentenceTask generateTaskWithRandomGenerator(SentenceDTO selectedSentence) {
        var selectedGeneratorIndex = Random.nextInRange(0, taskMap.size());
        var selectedGeneratorType = TaskType.values()[selectedGeneratorIndex];

        return generateTask(selectedGeneratorType, selectedSentence);
    }

    public TaskCheckResult checkResult(Integer userId, TaskResultDTO taskResult) {
        log.info("Extracting sentence with id -> {}", taskResult.taskId());
        var currentSentence = sentenceRepository.findById(taskResult.taskId());
        if (currentSentence.isEmpty()) {
            log.warn("Current sentence is empty");
            throw new RequestException("Current sentence not found");
        }

        log.info("Current sentence id -> {}", currentSentence.get().getId());

        var fullSentence = sentenceRepository.findById(currentSentence.get().getId());

        if (fullSentence.isEmpty()) {
            log.warn("Full sentence is empty");
            throw new RequestException("Full sentence not found");
        }
        log.info("Task result -> {}", taskResult);


        var checkResult = taskMap
                .get(taskResult.taskType())
                .checkTask(
                        userId,
                        taskResult.answer(),
                        fullSentence.get()
                );

        var userReference = userRepository.getReferenceById(userId);
        var statusReference = statusRepository.getReferenceById(checkResult.result().accepted() ? 2 : 1);
        var sentenceHistory = SentenceHistory.builder()
                .respondTime(50)
                .sentence(fullSentence.get())
                .status(statusReference)
                .user(userReference)
                .at(Instant.now())
                .build();
        sentenceHistoryRepository.save(sentenceHistory);

        return checkResult;
    }
}
