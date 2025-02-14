package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceHistory;
import org.zero.aienglish.model.UserStatisticDTO;

import java.util.Optional;

public interface SentenceHistoryRepository extends JpaRepository<SentenceHistory, Integer> {
    Optional<SentenceHistory> findFirstByUser_IdAndStatus_IdOrderByAtDesc(Integer userId, Integer status);

    Optional<SentenceHistory> findFirstByUser_IdOrderByAtAsc(Integer userId);

    @Query(value = """
            select
            	(COUNT(CASE WHEN status = 2 THEN 1 END) * 100.0 / COUNT(*)) AS acceptedPercentage,
            	sum(sh.respond_time) as spentTime
            from sentence_history sh
            where sh.user_id = 6 and date(sh.at) = DATE(now())
            """, nativeQuery = true)
    UserStatisticDTO getUserTodayStatistic(Integer userId);

    @Query(value = """
            WITH daily_usage AS (
                SELECT
                    DATE(sh.at) AS activity_date,
                    SUM(sh.respond_time) AS spend_time
                FROM sentence_history sh
                WHERE sh.user_id = ?1
                GROUP BY DATE(sh.at)
            ),
            filtered_days AS (
                SELECT
                    activity_date
                FROM daily_usage
                WHERE spend_time >= ?2
            ),
            streaks AS (
                SELECT
                    activity_date,
                    ROW_NUMBER() OVER (ORDER BY activity_date) AS day_num,
                    activity_date - INTERVAL '1 day' * ROW_NUMBER() OVER (ORDER BY activity_date) AS streak_group
                FROM filtered_days
            )
            SELECT
                COUNT(*) AS current_streak
            FROM streaks
            GROUP BY streak_group
            ORDER BY current_streak DESC
            LIMIT 1
            """, nativeQuery = true)
    Integer getDaysStreakWithDaySpentTime(Integer userId, Integer spentTime);

}
