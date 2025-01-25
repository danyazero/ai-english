package org.zero.aienglish.model;

import java.util.List;

public record ThemeSchemeDTO(
        List<Theme> themes,
        List<ThemeRely> relies
) {
}
