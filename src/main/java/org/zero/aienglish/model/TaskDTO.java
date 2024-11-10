package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.TenseEntity;

import java.util.List;

@Builder
public record TaskDTO(
        Integer id,
        String translate,
        String sentence,
        List<TenseEntity> tenses,
        List<WordResponseDTO> words
) {
}
