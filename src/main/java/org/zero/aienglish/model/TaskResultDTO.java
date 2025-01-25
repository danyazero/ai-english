package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record TaskResultDTO(
        TaskType taskType,
        Integer taskId,
        Integer respondTime,
        String answer
) {
}
