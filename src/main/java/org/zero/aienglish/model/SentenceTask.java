package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record SentenceTask(
        Integer sentenceId,
        TaskType taskType,
        String title,
        String caption,
        String pattern,
        String theme,
        Integer steps,
        List<WordResponseDTO> answers
) {
}
