package org.zero.aienglish.model;

import lombok.Builder;

@Builder
public record UserStatistic(
        Double todayAcceptedPercentage,
        String todaySpentTime,
        Integer daysStreak
) {
}
