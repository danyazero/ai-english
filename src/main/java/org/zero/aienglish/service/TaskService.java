package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.RecommendationState;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.TaskManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskManager taskManager;
    private final ThemeRepository themeRepository;
    private final SentenceRepository sentenceRepository;
    private final RecommendationRepository recommendationRepository;

    public SentenceTask getTask(Integer userId, Integer taskTheme) {
        if (taskTheme == null) {
            taskTheme = getCurrentThemeForUser(userId);
        }
        var selectedSentence = sentenceRepository.getSentenceForUser(userId, taskTheme);
        if (selectedSentence.isEmpty()) {
            log.warn("Sentence for task not found for user -> {}", userId);
            throw new RequestException("Sentence for task not found.");
        }


        log.info("Selected words is suitable. Selecting generator randomly.");

        return taskManager.generateTask(TaskType.GRAMMAR, selectedSentence.get());
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

    private Integer getCurrentThemeForUser(Integer userId) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isPresent()) {
        log.info("For user with id -> {}, current theme is -> {}, with current step -> {}", foundedState.get().getId(), foundedState.get().getCurrenThemeId(), foundedState.get().getStep());
            if (foundedState.get().getStep() >= 3) {
                var themeRating = foundedState.get().getTodayThemes();
                var nextTheme = getNextTheme(themeRating, foundedState.get());

                foundedState.get().setCurrenThemeId(nextTheme);
                foundedState.get().setStep(0);
                recommendationRepository.save(foundedState.get());

                return nextTheme;
            }

            return foundedState.get().getCurrenThemeId();
        }

        var todayRecommendedThemes = getTodayThemes(userId);
        log.info("Recommendation state first recommended for user -> {}", todayRecommendedThemes);
        var recommendationState = RecommendationState.builder()
                .currenThemeId(todayRecommendedThemes.getFirst())
                .todayThemes(todayRecommendedThemes)
                .timeToLive(72000)
                .id(userId)
                .step(0)
                .build();
        recommendationRepository.save(recommendationState);

        return todayRecommendedThemes.getFirst();
    }

    private List<Integer> getTodayThemes(Integer userId) {
        var recommendation = themeRepository.findUserThemeRating(userId, 5);
        if (recommendation.size() >= 5) {
            log.info("Recommendation for user -> {} prepaired on previous answers history", userId);
            var notAnsweredThemes = themeRepository.findThemeNoAnswered(userId, 1);
            recommendation.addAll(notAnsweredThemes);
        }

        var notAnsweredThemes = themeRepository.findThemeNoAnswered(userId, Math.max(0, 5 - recommendation.size()));
        recommendation.addAll(notAnsweredThemes);

        return recommendation.stream()
                .map(ThemeScoreDTO::getId)
                .toList();
    }

    private static Integer getNextTheme(List<Integer> themeRating, RecommendationState foundedState) {
        var isCurrentTheme = false;
        for (int i = 0; i < themeRating.size(); i++) {
            if (isCurrentTheme) {
                return themeRating.get(i);
            }
            if (themeRating.get(i).equals(foundedState.getCurrenThemeId())) {
                isCurrentTheme = true;
            }
        }

        return themeRating.getFirst();
    }


    public TaskCheckResult checkTask(Integer userId, TaskResultDTO taskResult) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isPresent()) {
            log.info("Recommendation state founded for user -> {}", userId);
            foundedState.get().setStep(foundedState.get().getStep() + 1);
            recommendationRepository.save(foundedState.get());
        }
        return taskManager.checkResult(userId, taskResult);
    }
}
