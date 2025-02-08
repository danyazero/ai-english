package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Themes(
        Pagination<ThemeDTO> recommendations,
        List<ThemeDTO> saved
) {
}
