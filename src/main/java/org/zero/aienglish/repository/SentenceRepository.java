package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceEntity;
import org.zero.aienglish.model.SentenceDTO;

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
}
