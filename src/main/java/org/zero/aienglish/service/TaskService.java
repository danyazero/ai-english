package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.Task;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.exception.SubscriptionExpiredException;
import org.zero.aienglish.mapper.AnswerMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.MarkCurrentKey;
import org.zero.aienglish.utils.MergeOmitPairs;
import org.zero.aienglish.utils.TaskManager;

import java.time.Instant;
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
    private final SubscriptonRepository subscriptonRepository;
    private final RecommendationRepository recommendationRepository;
    private final SentenceHistoryRepository sentenceHistoryRepository;

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
                .amountSteps(generatedTask.stepAmount())
                .task(List.of(generatedTask.pattern()))
                .translate(generatedTask.caption())
                .taskId(generatedTask.sentenceId())
                .answers(generatedTask.answers())
                .timeToLive(360)
                .currentStep(0)
                .id(userId)
                .build();
        taskRepository.save(taskState);


        return generatedTask;
    }

    public Optional<TaskState> revert(Integer userId) {
        var taskState = taskRepository.findById(userId);

        if (taskState.isEmpty()) {
            log.info("Task state not found for user -> {}", userId);

            return Optional.empty();
        }


        if (taskState.get().getTask().size() > 1) {
            revertState(taskState.get());
        }

        log.info("After revert current step is -> {}", taskState.get().getCurrentStep());
        var answers = taskState.get().getAnswers().stream()
                .filter(element -> Objects.equals(element.order(), taskState.get().getCurrentStep()))
                .toList();
        log.info("Answers for current step is -> {}", answers.size());

        return Optional.of(
                TaskState.builder()
                        .currentStep(taskState.get().getCurrentStep())
                        .amountSteps(taskState.get().getAmountSteps())
                        .title(taskState.get().getTask().getLast())
                        .caption(taskState.get().getTranslate())
                        .answers(answers)
                        .build()
        );
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

    public String getTaskTheoryHelp(Integer userId) {
        var currentTask = getCurrentTaskForTheory(userId);
        var taskSentence = sentenceRepository.findById(currentTask);
        if (taskSentence.isEmpty()) {
            log.warn("Sentence for task not found for taskId -> {}", currentTask);
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

    private Integer getCurrentTaskForTheory(Integer userId) {
        var state = taskRepository.findById(userId);
        if (state.isPresent()) {
            return state.get().getId();
        }

        var lastAnsweredTask = sentenceHistoryRepository.findByUser_IdOrderByAtAsc(userId);
        if (lastAnsweredTask.isEmpty()) {
            log.warn("Last answered task not found for user -> {}", userId);
            throw new RequestException("Last answered task not found.");
        }

        return lastAnsweredTask.get().getSentence().getId();
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


            updateRecommendationState(userId);
            var checkResult = taskManager.checkResult(userId, taskAnswer);

            var state = getTaskState(taskState.get(), nextStep);
            taskRepository.delete(taskState.get());

            return TaskCheckResult.builder()
                    .taskId(taskState.get().getTaskId())
                    .result(checkResult)
                    .checked(true)
                    .state(state)
                    .build();
        }

        var state = getTaskState(taskState.get(), nextStep);
        taskRepository.save(taskState.get());


        return TaskCheckResult.builder()
                .taskId(taskState.get().getTaskId())
                .state(state)
                .checked(false)
                .build();

    }

    private TaskState getTaskState(Task taskState, int nextStep) {
        var taskTranslate = taskState.getTranslate();
        var lastTaskState = taskState.getTask().getLast();
        var formattedTaskSentence = getFormattedTaskSentence(lastTaskState);

        var answers = getAnswersForStep(
                taskState.getAnswers(),
                nextStep
        );

        return TaskState.builder()
                .amountSteps(taskState.getAmountSteps())
                .title(formattedTaskSentence)
                .caption(taskTranslate)
                .currentStep(nextStep)
                .answers(answers)
                .build();
   }

    private TaskCheckResult generateTaskForUser(Integer userId) {
        var isActiveSubscription = subscriptonRepository.findFirstByUser_IdAndValidDueIsAfter(userId, Instant.now());
        if (isActiveSubscription.isEmpty()) {
            log.warn("User's subscription with id -> {}, has been expired", userId);
            throw new SubscriptionExpiredException("Строк дії вашої підписки закінчився.");
        }
        var generatedTask = getTask(userId);

        var formattedTaskTitle = getFormattedTaskSentence(generatedTask.title());


        log.info("Generated task answers -> {}", generatedTask.answers());
        var answers = getAnswersForStep(generatedTask.answers(), 0);
        log.info("Generated task answers for step 0 -> {}", answers);

        var state = TaskState.builder()
                .currentStep(generatedTask.currentStep())
                .amountSteps(generatedTask.stepAmount())
                .caption(generatedTask.caption())
                .title(formattedTaskTitle)
                .answers(answers)
                .build();

        return TaskCheckResult.builder()
                .taskId(generatedTask.sentenceId())
                .checked(false)
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
