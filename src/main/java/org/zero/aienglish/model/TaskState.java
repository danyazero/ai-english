package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record TaskState(
        String title,
        Integer currentStep,
        Integer amountSteps,
        List<TaskAnswer> answers
) {
}
