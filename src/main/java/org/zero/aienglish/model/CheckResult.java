package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.Vocabulary;

import java.util.List;

@Builder
public record CheckResult(
        Float mark,
        Boolean accepted,
        String correctAnswer
) {
}
