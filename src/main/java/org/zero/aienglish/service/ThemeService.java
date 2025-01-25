package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.entity.ThemeCategory;
import org.zero.aienglish.entity.ThemeTheme;
import org.zero.aienglish.model.Pagination;
import org.zero.aienglish.model.ThemeDTO;
import org.zero.aienglish.model.ThemeRely;
import org.zero.aienglish.model.ThemeSchemeDTO;
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
    private final ThemeCategoryRepository themeCategoryRepository;

    public List<ThemeDTO> getThemeCategories() {
        return themeCategoryRepository.findAll().stream()
                .map(mapThemeCategoryToThemeDTO())
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
