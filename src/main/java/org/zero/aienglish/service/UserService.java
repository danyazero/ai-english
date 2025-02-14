package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.Checkout;
import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.UserDTO;
import org.zero.aienglish.model.UserStatistic;
import org.zero.aienglish.repository.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CheckoutRepository checkoutRepository;
    private final SubscriptonRepository subscriptonRepository;
    private final SentenceHistoryRepository sentenceHistoryRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public UserStatistic getUserStatistic(Integer userId) {
        var userTodayStatistic = sentenceHistoryRepository.getUserTodayStatistic(userId);
        var daysStreak = sentenceHistoryRepository.getDaysStreakWithDaySpentTime(userId, 60);

        return UserStatistic.builder()
                .daysStreak(daysStreak)
                .todaySpentTime((userTodayStatistic.getSpentTime() / 60) + ":" + (userTodayStatistic.getSpentTime() % 60))
                .todayAcceptedPercentage(userTodayStatistic.getAcceptedPercentage())
                .build();
    }

    public Optional<Long> getUserTelegramId(Integer userId) {
        var foundedUser = userRepository.findById(userId);
        if (foundedUser.isEmpty()) {
            log.warn("User not found with id -> {}", userId);
            return Optional.empty();
        }

        return Optional.of(foundedUser.get().getTelegramUserId());
    }

    @Transactional
    public Integer getUserIdIfExistOrElseCreate(UserDTO user) {
        log.info("User -> {}", user);
        var founded = userRepository.findFirstByTelegramUserId(user.telegramId());
        if (founded.isPresent()) {
            log.warn("User already exist with id -> {}", founded.get().getId());
            return founded.get().getId();
        }

        var newUser = User.builder()
                .telegramUserId(user.telegramId())
                .username(user.username())
                .role("USER")
                .build();

        var createdUser = userRepository.save(newUser);

        var freeSubscriptionPlan = subscriptionPlanRepository.findFirstByNameIgnoreCase("безкоштовна");

        log.info("Founded free subscription -> {}", freeSubscriptionPlan);

        var checkout = Checkout.builder()
                .subscriptionPlan(freeSubscriptionPlan)
                .user(createdUser)
                .price(freeSubscriptionPlan.getPrice())
                .build();

        var savedCheckout = checkoutRepository.save(checkout);

        log.info("Saved checkout -> {}", savedCheckout);

        var freeSubscription = Subscription.builder()
                .validDue(Instant.now().plus(3, ChronoUnit.DAYS))
                .subscriptionPlan(freeSubscriptionPlan)
                .checkout(savedCheckout)
                .at(Instant.now())
                .user(createdUser)
                .build();

        subscriptonRepository.save(freeSubscription);
        log.info("User with id -> {}, got free premium", newUser.getId());

        return createdUser.getId();
    }

    public Integer updateUser(Integer userId, UserDTO user) {
        var founded = userRepository.findById(userId);
        if (founded.isEmpty()) {
            log.warn("User does not exist with id -> {}", userId);
            throw new RequestException("User does not exist with id -> " + userId);
        }
        founded.get().setUsername(user.username());
        return userRepository.save(founded.get()).getId();
    }
}
