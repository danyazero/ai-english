package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.RecommendationState;
import org.zero.aienglish.entity.Task;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.AnswerMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.MarkCurrentKey;
import org.zero.aienglish.utils.MergeOmitPairs;
import org.zero.aienglish.utils.TaskManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskManager taskManager;
    private final ThemeService themeService;
    private final TaskRepository taskRepository;
    private final MergeOmitPairs mergeOmitPairs;
    private final MarkCurrentKey markCurrentKey;
    private final SentenceRepository sentenceRepository;
    private final RecommendationRepository recommendationRepository;

    private SentenceTask getTask(Integer userId) {
        var taskTheme = themeService.getCurrentThemeForUser(userId);
        log.info("Current theme is {}", taskTheme);
        var selectedSentence = sentenceRepository.getSentenceForUser(userId, taskTheme);
        if (selectedSentence.isEmpty()) {
            log.warn("Sentence for task not found for user -> {}", userId);
            throw new RequestException("Sentence for task not found.");
        }


        log.info("Selected words is suitable. Selecting generator randomly.");

        var generatedTask = taskManager.generateTask(TaskType.GRAMMAR, selectedSentence.get());


        var taskState = Task.builder()
                .taskType(generatedTask.taskType().name())
                .task(List.of(generatedTask.pattern()))
                .taskId(generatedTask.sentenceId())
                .amountSteps(generatedTask.stepAmount())
                .answers(generatedTask.answers())
                .timeToLive(360)
                .currentStep(0)
                .id(userId)
                .build();
        taskRepository.save(taskState);


        return generatedTask;
    }

    public TaskState revert(Integer userId) {
        var taskState = taskRepository.findById(userId);

        if (taskState.isEmpty()) {
            log.info("Task state not found for user -> {}", userId);
            throw new RequestException("Task state not found.");
        }
        var updatedCurrentStep = revertState(taskState.get());

        var answers = taskState.get().getAnswers().stream()
                .filter(element -> Objects.equals(element.order(), updatedCurrentStep))
                .toList();

        return TaskState.builder()
                .amountSteps(taskState.get().getAmountSteps())
                .currentStep(updatedCurrentStep)
                .title(taskState.get().getTask().getLast())
                .answers(answers)
                .build();
    }

    private Integer revertState(Task taskState) {
        var taskList = taskState.getTask();
        taskList.removeLast();

        taskState.setTask(taskList);
        int updatedCurrentStep = taskState.getCurrentStep() - 1;
        taskState.setCurrentStep(updatedCurrentStep);
        taskRepository.save(taskState);


        return updatedCurrentStep;
    }


    private String getFormattedTaskSentence(String sentence) {
        return markCurrentKey.apply(
                mergeOmitPairs.apply(sentence)
        );
    }

    public String getTaskTheoryHelp(Integer taskId) {
        var taskSentence = sentenceRepository.findById(taskId);
        if (taskSentence.isEmpty()) {
            log.warn("Sentence for task not found for taskId -> {}", taskId);
            throw new RequestException("Sentence for task not found.");
        }

        return """
                %s
                
                <b>Пояснення до завдання:</b>
                %s
                """.formatted(
                taskSentence.get().getTheme().getCaption(),
                taskSentence.get().getExplanation()
        );
    }


    public TaskCheckResult checkTask(Integer userId, String answer) {
        var taskState = taskRepository.findById(userId);

        if (taskState.isEmpty() || answer.isBlank()) {
            log.info("Task not found for user -> {}", userId);
            return generateTaskForUser(userId);
        }

        updateTaskHistory(answer, taskState.get());
        var nextStep = increaseCurrentStep(taskState.get());

        if (isLastStep(nextStep, taskState.get())) {
            var taskAnswer = AnswerMapper.map(taskState.get());

            taskRepository.delete(taskState.get());
            updateRecommendationState(userId);

            return taskManager.checkResult(userId, taskAnswer);
        }

        return getTaskState(taskState.get(), nextStep);
    }

    private TaskCheckResult getTaskState(Task taskState, int nextStep) {
        var lastTaskState = taskState.getTask().getLast();
        var formattedTaskSentence = getFormattedTaskSentence(lastTaskState);

        var answers = getAnswersForStep(
                taskState.getAnswers(),
                nextStep
        );

        var taskStateResponse = TaskState.builder()
                .title(formattedTaskSentence)
                .amountSteps(taskState.getAmountSteps())
                .currentStep(nextStep)
                .answers(answers)
                .build();

        taskRepository.save(taskState);


        return TaskCheckResult.builder()
                .taskId(taskState.getTaskId())
                .state(taskStateResponse)
                .checked(false)
                .build();
    }

    private TaskCheckResult generateTaskForUser(Integer userId) {
        var generatedTask = getTask(userId);

        var formattedTaskTitle = getFormattedTaskSentence(generatedTask.title());

        var answers = getAnswersForStep(generatedTask.answers(), 0);

        var state = TaskState.builder()
                .title(formattedTaskTitle)
                .currentStep(generatedTask.currentStep())
                .amountSteps(generatedTask.stepAmount())
                .answers(answers)
                .build();

        return TaskCheckResult.builder()
                .checked(false)
                .taskId(generatedTask.sentenceId())
                .state(state)
                .build();
    }

    private static List<TaskAnswer> getAnswersForStep(List<TaskAnswer> taskState, int nextStep) {
        return taskState.stream()
                .filter(task -> task.order() == nextStep)
                .toList();
    }

    private static int increaseCurrentStep(Task taskState) {
        var nextStep = taskState.getCurrentStep() + 1;
        taskState.setCurrentStep(nextStep);

        return nextStep;
    }

    private void updateRecommendationState(Integer userId) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isPresent()) {
            log.info("Recommendation state founded for user -> {}", userId);
            foundedState.get().setStep(foundedState.get().getStep() + 1);
            recommendationRepository.save(foundedState.get());
        }
    }

    private static boolean isLastStep(int nextStep, Task taskState) {
        return nextStep >= taskState.getAmountSteps();
    }

    private static void updateTaskHistory(String answer, Task taskState) {
        var taskHistory = taskState.getTask();
        var task = taskHistory.getLast().replaceFirst("__", answer);
        taskHistory.add(task);

        taskState.setTask(taskHistory);
    }
}
