package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record SentenceTask(
        Integer sentenceId,
        TaskType taskType,
        String title,
        List<TenseDTO> tenses,
        List<WordResponseDTO> words,
        List<WordResponseDTO> answers
) {
}
