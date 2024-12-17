package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceHistoryRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskManager {
    private final Map<TaskType, TaskGenerator> taskMap;
    private final SentenceHistoryRepository sentenceHistoryRepository;

    public TaskManager(List<TaskGenerator> taskList, SentenceHistoryRepository sentenceHistoryRepository) {
        taskMap = taskList.stream().collect(Collectors.toMap(TaskGenerator::getTaskName, Function.identity()));
        this.sentenceHistoryRepository = sentenceHistoryRepository;
    }

    public SentenceTask generateTask(TaskType taskType, SentenceDTO selectedSentence) {
        return taskMap.get(taskType).generateTask(selectedSentence);
    }

    public SentenceTask generateTaskWithRandomGenerator(SentenceDTO selectedSentence) {
        log.info("Task types size -> {}", taskMap.size());
        var selectedGeneratorIndex = Random.nextInRange(0, taskMap.size());
        var selectedGeneratorType = TaskType.values()[selectedGeneratorIndex];
        log.info("Selected task type -> {}", selectedGeneratorType.name());

        return generateTask(selectedGeneratorType, selectedSentence);
    }

    public CheckResult checkResult(Integer userId, String taskResultDTO) {
        var logRow = sentenceHistoryRepository.findFirstByUser_IdAndStatus_IdOrderByAtDesc(userId, 1);
        if (logRow.isEmpty()) {
            log.warn("Sentence for check not found for user -> {}", userId);
            throw new RequestException("Sentence for check not found");
        }
        System.out.println();
        log.info("Answered task type -> {}", logRow.get().getTaskType().name());
        return taskMap
                .get(logRow.get().getTaskType())
                .checkTask(
                        userId,
                        taskResultDTO,
                        logRow.get().getSentence()
                );
    }
}
