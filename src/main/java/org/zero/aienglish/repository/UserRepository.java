package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.model.PairDTO;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findFirstByTelegramId(Long telegramId);
}
