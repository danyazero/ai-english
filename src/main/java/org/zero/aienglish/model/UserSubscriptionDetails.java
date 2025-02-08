package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record UserSubscriptionDetails(
        Optional<UserSubscription> userSubscription,
        List<SubscriptionPlan> availablePlans
) {
}
