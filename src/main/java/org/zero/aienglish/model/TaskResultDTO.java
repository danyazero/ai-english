package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record TaskResultDTO(
        Integer taskId,
        TaskType taskType,
        String answer
) {
}
