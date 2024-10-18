package org.zero.aienglish.model;

import java.util.List;

public record SentenceDTO(
        String sentence,
        String translate,
        String grammarTask,
        String[] tenseList,
        Integer theme
) {
}
