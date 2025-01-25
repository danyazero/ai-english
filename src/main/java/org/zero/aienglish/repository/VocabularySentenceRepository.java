package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.model.GrammarDTO;
import org.zero.aienglish.model.UserWordDTO;
import org.zero.aienglish.model.VocabularyDTO;

import java.util.List;

public interface VocabularySentenceRepository extends JpaRepository<VocabularySentence, Integer> {
    @Query("select v from VocabularySentence v where v.sentence.id = ?1 and v.vocabulary is not null order by v.order")
    List<VocabularySentence> getAllBySentenceIdOrderByOrder(Integer id);

    List<VocabularySentence> getAllByVocabulary_Id(Integer vocabularyId);

    @Query(value = """
            select
            v.*,
            (uv.id IS NOT NULL) as saved
            from vocabulary v, vocabulary_sentence vs
            left join user_vocabulary uv on uv.user_id = ?2 and uv.vocabulary_id = vs.vocabulary_id
            where vs.sentence_id = ?1 and v.id = vs.vocabulary_id
            """, nativeQuery = true)
    List<UserWordDTO> findAllBySentenceId(Integer sentenceId, Integer userId);

    @Query(value = """
            SELECT
                vs.sentence_id as id,
                vs.default_word as defaultWord,
                lower(v.word) as firstForm,
                lower(v.second) as secondForm,
                lower(v.third) as thirdForm,
                vs.is_modal as isModal
            FROM vocabulary_sentence vs
            LEFT JOIN vocabulary v on v.id = vs.vocabulary_id
            WHERE
                (vs.sentence_id IN (
                SELECT
                    s.id
                FROM
                    sentence s
                WHERE s.id != ?1
                LIMIT ?2
                )
            or vs.sentence_id = ?1)
            AND vs.is_marker = true
            order by vs."order"
            """, nativeQuery = true)
    List<GrammarDTO> findRandomGrammarWords(Integer ignoreSentenceId, Integer count);


    List<VocabularySentence> findAllBySentence_IdAndIsMarkerOrderByOrder(Integer sentenceId, Boolean isMarker);
}
