package org.zero.aienglish.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.entity.UserTheme;
import org.zero.aienglish.repository.ThemeRepository;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.repository.UserThemeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserThemeRepository userThemeRepository;

    @InjectMocks
    private ThemeService themeService;

    @Test
    public void testGetThemesByTitle() {
        String title = "Film";
        String searchValue = "%" + String.join("%", title.split("")) + "%";

        Theme theme1 = new Theme();
        theme1.setId(1);
        theme1.setTitle("Film 1");
        theme1.setYear(2020);

        Theme theme2 = new Theme();
        theme2.setId(2);
        theme2.setTitle("Film 2");
        theme2.setYear(2021);

        List<Theme> themes = List.of(theme1, theme2);
        when(themeRepository.findThemeByTitle(searchValue)).thenReturn(themes);

        List<Theme> result = themeService.getThemesByTitle(title);

        assertEquals(themes, result);
    }

    @Test
    public void testAddThemeToFavorite() {
        Integer userId = 1;
        Integer themeId_1 = 1;
        Integer themeId_2 = 2;

        User user = new User();
        user.setId(userId);
        user.setUsername("user1");

        Theme theme = new Theme();
        theme.setId(themeId_1);
        theme.setTitle("Film 1");
        theme.setYear(2020);

        Theme theme_2 = new Theme();
        theme_2.setId(themeId_2);
        theme_2.setTitle("Film 2");
        theme_2.setYear(2020);


        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(themeRepository.getReferenceById(themeId_1)).thenReturn(theme);
        when(themeRepository.getReferenceById(themeId_2)).thenReturn(theme_2);

        themeService.addThemeToFavorite(List.of(themeId_1, themeId_2), userId);

        ArgumentCaptor<List<UserTheme>> userThemeCaptor = ArgumentCaptor.forClass(List.class);
        verify(userThemeRepository).saveAll(userThemeCaptor.capture());

        List<UserTheme> capturedUserTheme = userThemeCaptor.getValue();

        assertEquals(2, capturedUserTheme.size());
        assertEquals(theme, capturedUserTheme.getFirst().getTheme());
        assertEquals(user, capturedUserTheme.getFirst().getUser());
        assertEquals(theme_2, capturedUserTheme.get(1).getTheme());
        assertEquals(user, capturedUserTheme.get(1).getUser());
    }

    @Test
    public void testDeleteThemeFromFavorite() {
        Integer userId = 1;
        Integer themeId = 1;

        List<Integer> themeIdList = List.of(themeId);
        themeService.deleteThemeFromFavorite(themeIdList, userId);

        verify(userThemeRepository).deleteByThemeIdAndUserId(themeIdList, userId);
    }
}