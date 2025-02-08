package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record UserSubscription(
        String planName,
        String start_date,
        String end_date,
        Double price,
        Integer remainDays,
        boolean isSubscription
) {
}
