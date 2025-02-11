package org.zero.aienglish.model;

import java.time.Instant;
import java.util.UUID;

public interface SubscriptionDTO {
    Integer getId();
    Instant getAt();
    Instant getValidDue();
    Integer getUserId();
    UUID getCheckoutId();
    Integer getSubscriptionPlanId();
}
