package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.entity.ThemeEntity;
import org.zero.aienglish.service.ThemeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/theme")
public class ThemeController {
    private final ThemeService themeService;

    @GetMapping
    public List<ThemeEntity> getPopularThemeList() {
        return themeService.getPopularThemeList();
    }

    @GetMapping("/{title}")
    public List<ThemeEntity> getThemeByTitle(@PathVariable String title) {
        return themeService.getThemesByTitle(title);
    }

    @PostMapping
    public void addThemeToUserFavorite(@RequestBody List<Integer> filmList, @RequestHeader("userId") Integer userId) {
        themeService.addThemeToFavorite(filmList, userId);
    }

    @DeleteMapping
    public void deleteThemeFromUserFavorite(@RequestBody List<Integer> filmList, @RequestHeader("userId") Integer userId) {
        themeService.deleteThemeFromFavorite(filmList, userId);
    }

}
