package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record TenseDTO(
        Integer id,
        String tense,
        String formula,
        String verb
) {}
