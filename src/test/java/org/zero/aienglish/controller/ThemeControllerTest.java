package org.zero.aienglish.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zero.aienglish.service.ThemeService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ThemeControllerTest {

    @Mock
    private ThemeService themeService;

    @InjectMocks
    private ThemeController themeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(themeController).build();
    }

    @Test
    public void testGetPopularThemeList() throws Exception {
        // Arrange
        when(themeService.getPopularThemeList()).thenReturn(List.of());

        // Act
        mockMvc.perform(get("/v1/api/theme"))
                .andExpect(status().isOk());

        // Assert
        verify(themeService).getPopularThemeList();
    }

    @Test
    public void testGetThemeByTitle() throws Exception {
        // Arrange
        String title = "test-tense";
        when(themeService.getThemesByTitle(title)).thenReturn(List.of());

        // Act
        mockMvc.perform(get("/v1/api/theme/{title}", title))
                .andExpect(status().isOk());

        // Assert
        verify(themeService).getThemesByTitle(title);
    }

    @Test
    public void testAddThemeToUserFavorite() throws Exception {
        // Arrange
        List<Integer> filmList = List.of(1, 2);
        Integer userId = 1;

        // Act
        mockMvc.perform(post("/v1/api/theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(filmList))
                        .header("userId", userId.toString()))
                .andExpect(status().isOk());

        // Assert
        verify(themeService).addThemeToFavorite(filmList, userId);
    }

    @Test
    public void testDeleteThemeFromUserFavorite() throws Exception {
        // Arrange
        List<Integer> filmList = List.of(1, 2);
        Integer userId = 1;

        // Act
        mockMvc.perform(delete("/v1/api/theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(filmList))
                        .header("userId", userId.toString()))
                .andExpect(status().isOk());

        // Assert
        verify(themeService).deleteThemeFromFavorite(filmList, userId);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
