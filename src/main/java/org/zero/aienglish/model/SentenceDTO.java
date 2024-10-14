package org.zero.aienglish.model;

public record SentenceDTO(
        String sentence,
        String translate,
        String grammarTask,
        Integer theme
) {
}
