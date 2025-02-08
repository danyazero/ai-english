package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record SubscriptionPlan(
        Integer id,
        String name,
        Integer durationDays,
        Double price,
        boolean isSubscription
) {
}
