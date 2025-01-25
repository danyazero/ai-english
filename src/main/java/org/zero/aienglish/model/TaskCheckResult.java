package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record TaskCheckResult(
        boolean accepted,
        String correctAnswer,
        String userAnswer,
        Float mark
) {
}
