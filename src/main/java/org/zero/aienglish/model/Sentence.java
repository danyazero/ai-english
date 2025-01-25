package org.zero.aienglish.model;

import java.util.List;

public record Sentence(
        String sentence,
        String translation,
        String sentenceTheme,
        String explanation,
        List<WordDTO> vocabulary
) {
}
