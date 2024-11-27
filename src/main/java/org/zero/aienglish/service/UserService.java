package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.UserEntity;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.Pair;
import org.zero.aienglish.model.UserDetails;
import org.zero.aienglish.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetails getUserData(Integer userId) {
        log.info("Reuested data for user with id -> {}", userId);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("User with id -> {} not found", userId);
            throw new RequestException("User with id -> {} not found");
        }
        return UserDetails.builder()
                .user(user.get())
                .completedToday(getTodayStat(userId))
                .build();
    }

    private List<Pair> getTodayStat(Integer userId) {
        var todayStat = userRepository.getTodayCompletedAndCompletedCorrect(userId);
        if (todayStat.isEmpty()) {
            log.warn("Today stat for user with id -> {} not found", userId);
            return Collections.emptyList();
        }
        var completedCorrectPercent = (todayStat.get().getCorrect() / todayStat.get().getCompleted()) * 100;
        log.info("Today stat for user with id -> {}, completed -> {}, completed correct -> {}, calculated value -> {}%", userId, todayStat.get().getCompleted(), todayStat.get().getCorrect(), completedCorrectPercent);
        var statMap = new ArrayList<Pair>();
        statMap.add(new Pair("Completed", String.valueOf(todayStat.get().getCompleted())));
        statMap.add(new Pair("Correct", String.format("%.1f", completedCorrectPercent) + "%"));

        return statMap;
    }
}
