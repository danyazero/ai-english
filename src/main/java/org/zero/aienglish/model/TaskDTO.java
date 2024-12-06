package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.Tense;

import java.util.List;

@Builder
public record TaskDTO(
        Integer id,
        String translate,
        String sentence,
        List<Tense> tenses,
        List<WordResponseDTO> words
) {
}
