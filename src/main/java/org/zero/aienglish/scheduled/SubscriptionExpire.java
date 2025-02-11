package org.zero.aienglish.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.model.NotificationDTO;
import org.zero.aienglish.model.SubscriptionDTO;
import org.zero.aienglish.repository.SubscriptionRepository;
import org.zero.aienglish.service.NotificationService;
import org.zero.aienglish.utils.FormatDate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpire {
    private final NotificationService notificationService;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(fixedDelay = 720, timeUnit = TimeUnit.MINUTES)
    private void notifyUsersSubscriptionExpire() {
        log.info("Scheduled task started.");
        var subscriptionsExpireSoon = subscriptionRepository.findAllWithExpireSoon();
        for (var subscription : subscriptionsExpireSoon) {
            var userId = subscription.getUserId();

            var notification = NotificationDTO.builder()
                    .recipientId(userId)
                    .message(getSubscriptionExpireMessage(subscription))
                    .build();
            notificationService.sendNotification(notification);
        }
    }

    private static String getSubscriptionExpireMessage(SubscriptionDTO subscription) {
        var timeLeft = ChronoUnit.HOURS.between(Instant.now(), subscription.getValidDue());
        return "<b>⌛ Ваша підписка завершується через - " + timeLeft + "год.</b>\n\nЩоб і надалі користуватися всіма перевагами, продовжіть підписку заздалегідь.\n\n<i>Ви можете зробити це у вашому профілі.</i>";
    }
}
