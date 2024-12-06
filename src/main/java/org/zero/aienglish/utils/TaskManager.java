package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.zero.aienglish.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TaskManager {
    private final Map<TaskType, TaskGenerator> taskMap;

    public TaskManager(List<TaskGenerator> taskList) {
        taskMap = taskList.stream().collect(Collectors.toMap(TaskGenerator::getTaskName, Function.identity()));
    }

    public SentenceTask generateTask(TaskType taskType, SentenceDTO selectedSentence) {
        return taskMap.get(taskType).generateTask(selectedSentence);
    }

    public SentenceTask generateTaskWithRandomGenerator(SentenceDTO selectedSentence) {
       var selectedGeneratorIndex = Random.nextInRange(0, taskMap.size());
       var selectedGeneratorType = TaskType.values()[selectedGeneratorIndex];

       return generateTask(selectedGeneratorType, selectedSentence);
    }

    public CheckResult checkResult(Integer userId, TaskResultDTO taskResultDTO) {
        return taskMap.get(taskResultDTO.taskType()).checkTask(userId, taskResultDTO);
    }
}
