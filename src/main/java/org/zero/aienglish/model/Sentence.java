package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Sentence(
        String sentence,
        String translation,
        String sentenceTheme,
        String explanation,
        List<WordDTO> vocabulary
) {
}
