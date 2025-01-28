package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record CheckResultDTO(
        boolean accepted,
        String userAnswer,
        String correctAnswer,
        Float mark
) {
}
