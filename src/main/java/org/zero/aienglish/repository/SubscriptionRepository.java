package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.model.SubscriptionDTO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findFirstByUserIdOrderByAtDesc(Integer userId);
    @Query("select s from Subscription s where s.user.id = ?1 and s.validDue >= CURRENT_TIMESTAMP")
    List<Subscription> findFirstByActualSubscriptions(Integer userId);

    long countByUserId(Integer userId);

    @Query(value = """
            select
            	*
            from subscription s
            where s.valid_due >= now() and s.valid_due <= now() + INTERVAL '12 HOUR'
            """, nativeQuery = true)
    List<SubscriptionDTO> findAllWithExpireSoon();
}
