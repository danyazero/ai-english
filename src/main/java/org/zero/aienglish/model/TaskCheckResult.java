package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record TaskCheckResult(
        Integer taskId,
        boolean checked,
        CheckResultDTO result,
        TaskState state
) {
}
