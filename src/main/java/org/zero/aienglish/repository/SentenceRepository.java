package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceEntity;
import org.zero.aienglish.model.SentenceDTO;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<SentenceEntity, Integer> {
    Optional<SentenceEntity> findFirstBySentenceLikeIgnoreCase(String sentence);

    @Query(value = """
            with SentenceHistory as (
                select\s
                    S.id,
                    S.translate,
                    S.sentence,
                    SH.last_answered,
                    (select avg(sus.accuracy) from sentence_user_history sus where sus.sentence_id = S.id) as angg,
                    row_number() over (partition by S.id order by SH.last_answered desc) as rn
                from user_theme US
                join sentence S on S.theme = US.theme_id
                left join sentence_user_history SH on SH.user_id = ?1 and SH.sentence_id = S.id
                where US.user_id = ?1
            )
            select id, translate, sentence, angg as score
            from SentenceHistory
            where rn = 1
            order by last_answered nulls first
            limit 1
            """, nativeQuery = true)
    Optional<SentenceDTO> getSentenceForUser(Integer userId);

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
