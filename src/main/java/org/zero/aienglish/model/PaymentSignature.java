package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record PaymentSignature(
        String data,
        String signature
) {
}
