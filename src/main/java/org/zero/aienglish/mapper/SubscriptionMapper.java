package org.zero.aienglish.mapper;

import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.model.SubscriptionPlan;
import org.zero.aienglish.model.UserSubscription;
import org.zero.aienglish.model.UserSubscriptionDetails;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SubscriptionMapper {
    public static SubscriptionPlan map(org.zero.aienglish.entity.SubscriptionPlan element) {
        return SubscriptionPlan.builder()
                .id(element.getId())
                .durationDays(element.getDurationDays())
                .price(element.getPrice())
                .name(element.getName())
                .build();
    }

    public static org.zero.aienglish.lib.grpc.Subscription.SubscriptionPlan map(SubscriptionPlan element) {
        return org.zero.aienglish.lib.grpc.Subscription.SubscriptionPlan.newBuilder()
                .setId(element.id())
                .setName(element.name())
                .setDurationDays(element.durationDays())
                .setPrice(element.price())
                .build();
    }

    public static org.zero.aienglish.lib.grpc.Subscription.UserSubscriptionPlan map(UserSubscription userSubscription) {
        return org.zero.aienglish.lib.grpc.Subscription.UserSubscriptionPlan.newBuilder()
                .setRemainDays(userSubscription.remainDays())
                .setStartDate(userSubscription.start_date())
                .setEndDate(userSubscription.end_date())
                .setName(userSubscription.planName())
                .setPrice(userSubscription.price())
                .build();
    }

    public static UserSubscription map(Subscription subscription) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());

        var formattedStartDate = formatter.format(subscription.getAt());
        var formattedEndDate = formatter.format(subscription.getValidDue());
        var daysRemain = ChronoUnit.DAYS.between(Instant.now(), subscription.getValidDue());

        return UserSubscription.builder()
                .planName(subscription.getSubscriptionPlan().getName())
                .start_date(formattedStartDate)
                .end_date(formattedEndDate)
                .remainDays((int) daysRemain)
                .price(subscription.getSubscriptionPlan().getPrice())
                .build();
    }
}
