package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record UserDTO(
        Long telegramId,
        String username
) { }
