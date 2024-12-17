package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record CheckResult(
        Float mark,
        Boolean accepted,
        String correctAnswer
) {
}
