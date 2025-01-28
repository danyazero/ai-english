package org.zero.aienglish.model;

import lombok.*;

@Builder
public record TaskAnswer (
    String word,
    Integer order
){}
