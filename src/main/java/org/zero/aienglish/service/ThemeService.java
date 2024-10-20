package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.entity.UserTheme;
import org.zero.aienglish.mapper.ThemeMapper;
import org.zero.aienglish.repository.ThemeRepository;
import org.zero.aienglish.repository.UserThemeRepository;
import org.zero.aienglish.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final UserThemeRepository userThemeRepository;
    private final ThemeMapper themeMapper;

    public List<Theme> getPopularThemeList() {
        var popularThemes = themeRepository.findPopularThemeList();

        return popularThemes.stream().map(themeMapper::map).toList();
    }

    public List<Theme> getThemesByTitle(String title) {
        var searchValue = "%" + String.join( "%", title.split("")) + "%";
        return themeRepository.findThemeByTitle(searchValue);
    }

    public void addThemeToFavorite(List<Integer> themeIdList, Integer userId) {
        var user = userRepository.getReferenceById(userId);
        var userFavoriteThemeList = themeIdList.stream()
                .map(themeRepository::getReferenceById)
                .map(film -> new UserTheme(film, user))
                .toList();
        userThemeRepository.saveAll(userFavoriteThemeList);
    }

    public void deleteThemeFromFavorite(List<Integer> filmList, Integer userId) {
        userThemeRepository.deleteByThemeIdAndUserId(filmList, userId);
    }
}
