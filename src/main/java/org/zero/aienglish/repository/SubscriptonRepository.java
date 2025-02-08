package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Subscription;

import java.time.Instant;
import java.util.Optional;

public interface SubscriptonRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findFirstByUser_IdAndValidDueIsAfter(Integer userId, Instant validDueBefore);
}
