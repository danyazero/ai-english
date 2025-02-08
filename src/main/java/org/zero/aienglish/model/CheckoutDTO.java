package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record CheckoutDTO(
        SubscriptionPlan subscriptionPlan,
        String checkoutLink
) {
}
