package org.zero.aienglish.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public record TaskWord(
        String word,
        String defaultWord,
        boolean isModal,
        Integer order
) {
}
