package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.VocabularyWordDTO;

import java.util.List;
import java.util.Optional;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {
    @Query(value = "SELECT * FROM vocabulary f WHERE LOWER(f.word) ILIKE ANY(ARRAY[?1])", nativeQuery = true)
List<Vocabulary> findAllByWord(String[] word);

    @Query(value = """
    SELECT
        v.id,
        v.word,
        v.translate,
        sp.title as speechPartTitle,
        sp.translate as speechPartTranslate,
        sp.answers_to as speechPartAnswers
    FROM user_vocabulary uv
    join vocabulary v on v.id = uv.vocabulary_id
    join speech_part sp on sp.id = v.speech_part_id
    where uv.user_id = 3
    limit 1
""", nativeQuery = true)
    Optional<VocabularyWordDTO> getVocabularyWordForUser(int userId);
}
