package org.zero.aienglish.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.model.ThemeScoreDTO;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    Optional<Theme> findFirstByTitle(String title);

    Page<Theme> findAllByCategory_Id(Integer categoryId, Pageable pageable);

//    @Cacheable("userThemeRating")
    @Query(value = """
            SELECT
                t.id AS id,
                t.title AS title,
                AVG(CASE WHEN sh.status = s.id AND s.title = 'CORRECT' THEN 1 ELSE 0 END) AS score
            FROM
                sentence_history sh
            JOIN
                sentence snt ON sh.sentence_id = snt.id
            JOIN
                theme t ON snt.theme_id = t.id
            JOIN
                status s ON sh.status = s.id
            WHERE
                sh.user_id = ?1
            GROUP BY
                t.id, t.title
            ORDER BY
                score
            LIMIT ?2
            """, nativeQuery = true)
    List<ThemeScoreDTO> findUserThemeRating(Integer userId, Integer limit);

    @Query(value = """
            SELECT distinct
                t.id as id,
                t.title as title,
                0 as score
            from theme t, sentence s
            LEFT JOIN sentence_history sh ON sh.sentence_id = s.id and sh.user_id = ?1
            where s.theme_id = t.id and sh.at IS NULL
            LIMIT ?2;
            """, nativeQuery = true)
    List<ThemeScoreDTO> findThemeNoAnswered(Integer userId, Integer limit);
}
