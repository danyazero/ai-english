package org.zero.aienglish.mapper;

import lombok.extern.slf4j.Slf4j;
import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.model.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class SubscriptionMapper {
    public static SubscriptionPlan mapToGrpc(org.zero.aienglish.entity.SubscriptionPlan element) {
        return SubscriptionPlan.builder()
                .id(element.getId())
                .durationDays(element.getDurationDays())
                .price(element.getPrice())
                .name(element.getName())
                .build();
    }

    public static org.zero.aienglish.lib.grpc.Subscription.SubscriptionPlan mapToGrpc(SubscriptionPlan element) {
        return org.zero.aienglish.lib.grpc.Subscription.SubscriptionPlan.newBuilder()
                .setId(element.id())
                .setName(element.name())
                .setDurationDays(element.durationDays())
                .setPrice(element.price())
                .build();
    }

    public static TelegramCallbackButton map(SubscriptionPlan plan) {
        return TelegramCallbackButton.builder()
                .text(plan.name() + " • " + plan.price() + "UAH")
                .callback(TelegramCallbackEnum.PLAN_CHECKOUT.name() + " " + plan.id())
                .build();
    }

    public static org.zero.aienglish.lib.grpc.Subscription.UserSubscriptionPlan mapToGrpc(UserSubscription userSubscription) {
        return org.zero.aienglish.lib.grpc.Subscription.UserSubscriptionPlan.newBuilder()
                .setRemainDays(userSubscription.remainDays())
                .setStartDate(userSubscription.start_date())
                .setEndDate(userSubscription.end_date())
                .setName(userSubscription.planName())
                .setPrice(userSubscription.price())
                .build();
    }

    public static UserSubscription mapToGrpc(List<Subscription> subscription) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());

        var startDate = subscription.stream()
                .min(Comparator.comparing(Subscription::getAt));

        if (startDate.isEmpty()) {
            log.warn("Minimal start date is empty");
            throw new IllegalArgumentException("Minimal start date is empty");
        }

        var endDate = subscription.stream()
                .max(Comparator.comparing(Subscription::getValidDue));

        var formattedStartDate = formatter.format(startDate.get().getAt());
        var formattedEndDate = formatter.format(endDate.get().getValidDue());
        var daysRemain = ChronoUnit.DAYS.between(Instant.now(), endDate.get().getValidDue());

        double amountPrice = subscription.stream()
                .mapToDouble(element -> element.getCheckout().getPrice())
                .sum();

        return UserSubscription.builder()
                .planName(getPlanName(subscription))
                .start_date(formattedStartDate)
                .end_date(formattedEndDate)
                .remainDays((int) daysRemain)
                .price(amountPrice)
                .build();
    }

    private static String getPlanName(List<Subscription> subscription) {
        return subscription.size() > 1 ? "Декілька (" + subscription.size() + ")" : subscription.getFirst().getSubscriptionPlan().getName();
    }
}
