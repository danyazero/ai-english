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
import org.zero.aienglish.repository.CheckoutRepository;
import org.zero.aienglish.repository.SubscriptionPlanRepository;
import org.zero.aienglish.repository.SubscriptonRepository;
import org.zero.aienglish.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CheckoutRepository checkoutRepository;
    private final SubscriptonRepository subscriptonRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public Integer createUser(UserDTO user) {
        var founded = userRepository.findFirstByUsername(user.username());
        if (founded.isPresent()) {
            log.warn("User already exist with id -> {}", founded.get().getId());
            return founded.get().getId();
        }

        var newUser = User.builder()
                .username(user.username())
                .role("USER")
                .build();

        var createdUser = userRepository.save(newUser);

        var freeSubscriptionPlan = subscriptionPlanRepository.findFirstByNameIgnoreCase("безкоштовна");

        log.info("Founded free subscription -> {}", freeSubscriptionPlan);

        var checkout = Checkout.builder()
                .subscriptionPlan(freeSubscriptionPlan)
                .user(createdUser)
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
