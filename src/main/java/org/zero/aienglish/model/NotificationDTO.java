package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record NotificationDTO(
        Integer recipientId,
        String message
) {
}
