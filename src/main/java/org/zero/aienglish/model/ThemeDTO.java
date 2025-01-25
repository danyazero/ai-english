package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record ThemeDTO(
        Integer id,
        String title
) {
}
