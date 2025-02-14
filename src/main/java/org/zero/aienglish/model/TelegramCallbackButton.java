package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record TelegramCallbackButton(
        String callback,
        String text
) {
}
