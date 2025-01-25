package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.model.SentenceDTO;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    Optional<Sentence> findFirstBySentenceLikeIgnoreCase(String sentence);

    @Query(value = """
            SELECT s.id, s.sentence, s.translate, t.id as themeId, t.title as theme
            FROM sentence s
            LEFT JOIN sentence_history sh ON s.id = sh.sentence_id and sh.user_id = ?1
            JOIN theme t on t.id = s.theme_id
            WHERE (?2 IS NULL OR s.theme_id = ?2)
            GROUP BY s.id, s.sentence, t.id, title
            ORDER BY MAX(sh.at) NULLS FIRST
            LIMIT 1
            """, nativeQuery = true)
    Optional<SentenceDTO> getSentenceForUser(Integer userId, Integer themeId);

    @Query(value = """
select s.id, s.sentence, s.translate
from vocabulary_sentence vs, sentence s
where vs.vocabulary_id = ?1 and s.id = vs.sentence_id
limit 3
""", nativeQuery = true)
    List<SentenceDTO> getSentencesForWord(Integer wordId);

    @Query(value = """
select DISTINCT S.*
from sentence S
join (
    select VS.sentence_id
    from vocabulary_sentence VS
    where VS.vocabulary_id != 6
    order by random()
    limit 5
) sel on S.id = sel.sentence_id
limit 3
""", nativeQuery = true)
    List<SentenceDTO> getRandomSentencesExceptWord(Integer wordId);

}
