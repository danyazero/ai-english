package org.zero.aienglish.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CheckoutDTO(
        SubscriptionPlan subscriptionPlan,
        String checkoutLink,
        Instant validDue
) {
}
