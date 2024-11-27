package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record VocabularySentence(
        Integer id,
        String sentence,
        String translation
) {
}
