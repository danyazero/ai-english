package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.RecommendationState;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.entity.ThemeCategory;
import org.zero.aienglish.entity.ThemeTheme;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.RecommendationRepository;
import org.zero.aienglish.repository.ThemeCategoryRepository;
import org.zero.aienglish.repository.ThemeRelyRepository;
import org.zero.aienglish.repository.ThemeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ThemeRelyRepository themeRelyRepository;
    private final RecommendationRepository recommendationRepository;
    private final ThemeCategoryRepository themeCategoryRepository;

    public List<ThemeDTO> getThemeCategories() {
        return themeCategoryRepository.findAll().stream()
                .map(mapThemeCategoryToThemeDTO())
                .toList();
    }

    public void selectTheme(Integer userId, Integer themeId) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isEmpty()) {
            foundedState = Optional.of(initTodayRecommendationState(userId));
        }

        if (foundedState.get().getSelectedThemes() == null) {
            foundedState.get().setSelectedThemes(List.of(themeId));
        } else {
            foundedState.get().getSelectedThemes().add(themeId);
        }
        recommendationRepository.save(foundedState.get());
    }

    public void clearTheme(Integer userId) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isEmpty()) throw new RequestException("Recommendation is empty");

        foundedState.get().setSelectedThemes(List.of());
        recommendationRepository.save(foundedState.get());
    }

    public Integer getCurrentThemeForUser(Integer userId) {
        var foundedState = recommendationRepository.findById(userId);
        if (foundedState.isPresent()) {
            if (foundedState.get().getSelectedThemes() != null) {
                return getNextSelectedTheme(foundedState.get());
            }
            if (foundedState.get().getStep() >= 3) {
                var nextTheme = getNextRecommendedTheme(foundedState.get());

                foundedState.get().setStep(0);
                recommendationRepository.save(foundedState.get());

                return nextTheme;
            }

            return foundedState.get().getCurrenThemeId();
        }

        var todayRecommendedThemes = initTodayRecommendationState(userId);

        return todayRecommendedThemes.getTodayThemes().getFirst();
    }

    private RecommendationState initTodayRecommendationState(Integer userId) {
        var todayRecommendedThemes = getTodayThemes(userId);
        log.info("Recommendation state first recommended for user -> {}", todayRecommendedThemes);
        var recommendationState = RecommendationState.builder()
                .currenThemeId(todayRecommendedThemes.getFirst())
                .todayThemes(todayRecommendedThemes)
                .currentThemeIndex(0)
                .timeToLive(72000)
                .id(userId)
                .step(0)
                .build();
        return recommendationRepository.save(recommendationState);
    }

    private static Integer getNextSelectedTheme(RecommendationState foundedState) {
        foundedState.setCurrentThemeIndex(foundedState.getCurrentThemeIndex() + 1);

        if (foundedState.getCurrentThemeIndex() >= foundedState.getSelectedThemes().size()) {
            foundedState.setCurrentThemeIndex(0);
        }
        var currentIndex = foundedState.getCurrentThemeIndex();

        return foundedState.getSelectedThemes().get(currentIndex);
    }

    private static int getNextRecommendedTheme(RecommendationState foundedState) {
        var themeRating = foundedState.getTodayThemes();
        var nextThemeIndex = getNextThemeIndex(foundedState, themeRating);
        foundedState.setCurrentThemeIndex(nextThemeIndex);

        var nextTheme = themeRating.get(nextThemeIndex);
        foundedState.setCurrenThemeId(nextTheme);

        return nextTheme;
    }

    private static int getNextThemeIndex(RecommendationState foundedState, List<Integer> themeRating) {
        return foundedState.getCurrentThemeIndex() + 1 >= themeRating.size() ? 0 : foundedState.getCurrentThemeIndex() + 1;
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

    public void addThemes(ThemeSchemeDTO themeScheme) {
        var savedThemes = new ArrayList<Theme>();
        for (var theme : themeScheme.themes()) {
            var savedTheme = addTheme(theme);
            savedThemes.add(savedTheme);
        }
        for (ThemeRely rely : themeScheme.relies()) {
            var firstTheme = findThemeByTitle(savedThemes, rely.theme());
            if (firstTheme.isEmpty()) continue;

            var secondTheme = findThemeByTitle(savedThemes, rely.theme());
            if (secondTheme.isEmpty()) continue;

            var themeRely = ThemeTheme.builder()
                    .theme(firstTheme.get())
                    .rely(secondTheme.get())
                    .build();
            themeRelyRepository.save(themeRely);
        }
    }

    private Optional<Theme> findThemeByTitle(List<Theme> themes, String title) {
        var founded = findThemeByTitleInSaved(themes, title);
        if (founded.isEmpty()) {
            return themeRepository.findFirstByTitle(title);
        }

        return founded;
    }

    private static Optional<Theme> findThemeByTitleInSaved(List<Theme> themes, String title) {
        return themes.stream()
                .filter(theme -> theme.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    private Theme addTheme(org.zero.aienglish.model.Theme theme) {
        var foundedTheme = themeRepository.findFirstByTitle(theme.title());
        if (foundedTheme.isPresent()) {
            log.info("Theme with title -> {}, already exists", theme.title());
            return foundedTheme.get();
        }
        var categoryReference = themeCategoryRepository.getReferenceById(theme.categoryId());

        var newTheme = Theme.builder()
                .title(theme.title())
                .caption(theme.caption() == null ? "--" : theme.caption())
                .category(categoryReference)
                .build();

        return themeRepository.save(newTheme);
    }

    public Pagination<ThemeDTO> getThemeForCategory(Integer categoryId, Integer page) {
        var pageParams = PageRequest.of(page, 2);
        var themePage = themeRepository.findAllByCategory_Id(categoryId, pageParams);

        var foundedThemes = themePage.stream()
                .map(mapThemeToThemeDTO())
                .toList();

        return Pagination.<ThemeDTO>builder()
                .totalPages(themePage.getTotalPages())
                .items(foundedThemes)
                .currentPage(page)
                .build();
    }

    private static Function<Theme, ThemeDTO> mapThemeToThemeDTO() {
        return element -> ThemeDTO.builder()
                .id(element.getId())
                .title(element.getTitle())
                .build();
    }

    private static Function<ThemeCategory, ThemeDTO> mapThemeCategoryToThemeDTO() {
        return element -> ThemeDTO.builder()
                .id(element.getId())
                .title(element.getTitle())
                .build();
    }
}
