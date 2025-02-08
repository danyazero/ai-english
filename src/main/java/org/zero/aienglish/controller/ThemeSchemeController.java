package org.zero.aienglish.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.model.ThemeSchemeDTO;
import org.zero.aienglish.service.ThemeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/theme")
public class ThemeSchemeController {
    private final ThemeService themeService;

    @PutMapping
    public void addThemes(@RequestBody ThemeSchemeDTO themeScheme) {
        themeService.addThemes(themeScheme);
    }
}
