package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.UserWordDTO;
import org.zero.aienglish.model.UserWordFullDTO;
import org.zero.aienglish.model.VocabularyWordDTO;

import java.util.List;
import java.util.Optional;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {
    @Query(value = "SELECT * FROM vocabulary f WHERE LOWER(f.word) ILIKE ANY(ARRAY[?1])", nativeQuery = true)
    List<Vocabulary> findAllByWord(String[] word);

    @Query(value = """
            select v.id, v.word, v.translate, v.meaning, (uv is not null) as saved
            from vocabulary v
            left join user_vocabulary uv on uv.user_id = ?1 and uv.vocabulary_id = v.id
            where v.id = ?2
            """, nativeQuery = true)
    Optional<UserWordFullDTO> getWordForUser(Integer userId, Integer wordId);

    @Query(value = """
            select
                v.*,
                (uv.id IS NOT NULL) as saved
            from vocabulary v
            left join user_vocabulary uv on uv.vocabulary_id = v.id
            where uv.user_id = ?1
            order by v.id
            limit ?2 offset ?2 * ?3
            """, nativeQuery = true)
    List<UserWordDTO> getUserVocabularyList(Integer userId, Integer size, Integer page);

    @Query("select count(uv.id) from UserVocabulary uv where uv.user.id=?1")
    Integer getTotalPages(Integer userId);

    @Query(value = """
            select
            v.*,
            (uv.id IS NOT NULL) as saved
            from vocabulary v
            left join user_vocabulary uv on uv.user_id = ?2 and uv.vocabulary_id = v.id
            where upper(v.word) like upper(('%'||?1||'%')) escape '' or upper(v.translate) like upper(('%'||?1||'%')) escape ''
            order by v.id
            """, nativeQuery = true)
    List<UserWordDTO> getBySearchKey(String word, Integer userId);

    @Query("""
            select v from Vocabulary v
            where v.id not in (
                select uv.word.id from UserVocabulary  uv
                where uv.user.id = ?1
                )
            """)
    List<Vocabulary> getWordsWhichNotSaved(Integer userId);

    @Query(value = """
            SELECT
                v.id,
                v.word,
                v.translate, 
                sp.title
            FROM
                vocabulary v, speech_part sp
            WHERE
                v.id NOT IN (
                    SELECT
                        vs.vocabulary_id
                    FROM
                        vocabulary_history vs
                    WHERE
                        vs.status IN (2, 3)
                        AND vs.user_id = 3
                ) and sp.id = v.speech_part_id
            limit 1;
            """, nativeQuery = true)
    Optional<VocabularyWordDTO> getVocabulary(Integer userId);

    @Query(value = """
                SELECT
                    v.id,
                    v.word,
                    v.translate,
                    sp.title as speechPartTitle,
                FROM user_vocabulary uv
                join vocabulary v on v.id = uv.vocabulary_id
                join speech_part sp on sp.id = v.speech_part_id
                where uv.user_id = 3
                limit 1
            """, nativeQuery = true)
    Optional<VocabularyWordDTO> getVocabularyWordForUser(int userId);
}
