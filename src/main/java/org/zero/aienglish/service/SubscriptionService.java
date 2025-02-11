package org.zero.aienglish.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.mapper.SubscriptionMapper;
import org.zero.aienglish.model.CheckoutDTO;
import org.zero.aienglish.model.UserSubscription;
import org.zero.aienglish.model.UserSubscriptionDetails;
import org.zero.aienglish.repository.SubscriptionPlanRepository;
import org.zero.aienglish.repository.SubscriptionRepository;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {
    private final PaymentService paymentService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public UserSubscriptionDetails getUserSubscriptionDetails(Integer userId) {
        var userSubscription = getUserSubscription(userId);

        var availablePaidSubscriptionPlans = subscriptionPlanRepository.findAllPaidPlans().stream()
                .map(SubscriptionMapper::map)
                .toList();

        log.info("Available paid plans successfully extracted -> {}", availablePaidSubscriptionPlans.size());

        return UserSubscriptionDetails.builder()
                .availablePlans(availablePaidSubscriptionPlans)
                .userSubscription(userSubscription)
                .build();
    }

    public CheckoutDTO getSubscriptionPlanCheckout(Integer userId, Integer planId) {
        return paymentService.getPaymentCheckout(userId, planId);
    }

    private Optional<UserSubscription> getUserSubscription(Integer userId) {
        var subscription = subscriptionRepository.findFirstByActualSubscriptions(userId);

        if (subscription.isEmpty()) {
            log.info("User dont have subscription.");

            return Optional.empty();
        }

        return Optional.of(SubscriptionMapper.map(subscription));
    }
}
