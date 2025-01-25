package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record TaskDTO(
        Integer id,
        String translate,
        String sentence,
        List<WordResponseDTO> words
) {
}
