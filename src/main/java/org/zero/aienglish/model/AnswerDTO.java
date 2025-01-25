package org.zero.aienglish.model;

import lombok.Builder;

import java.util.Optional;

@Builder
public record AnswerDTO(
        Integer order,
        Integer sentenceId,
        GrammarDTO word,
        Optional<TaskWord> correct,
        boolean isNegative
) {
}
