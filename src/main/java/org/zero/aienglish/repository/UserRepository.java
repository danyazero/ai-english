package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.UserEntity;
import org.zero.aienglish.model.PairDTO;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findFirstByEmail(String email);

    @Query(value = """
            SELECT
                COUNT(suh.id) AS completed,
                (SELECT COUNT(su.id)
                 FROM sentence_user_history su
                 WHERE su.last_answered >= CURRENT_DATE
                   AND su.last_answered < CURRENT_DATE + INTERVAL '1 DAY'
                   AND su.accuracy >= 98 and su.user_id = ?1) AS correct
            FROM sentence_user_history suh
            WHERE suh.last_answered >= CURRENT_DATE
              AND suh.last_answered < CURRENT_DATE + INTERVAL '1 DAY' and suh.user_id = ?1
            """, nativeQuery = true)
    Optional<PairDTO> getTodayCompletedAndCompletedCorrect(Integer userId);
}
