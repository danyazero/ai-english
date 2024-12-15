package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.model.VocabularyDTO;

import java.util.List;
import java.util.Optional;

public interface VocabularySentenceRepository extends JpaRepository<VocabularySentence, Integer> {
    List<VocabularySentence> getAllBySentenceIdOrderByOrder(Integer id);


    List<VocabularySentence> getAllByVocabulary_Id(Integer id);
    @Query(value = """
SELECT
    V.id,
    VS.default_word as word,
    V.translate as translate,
    SP.title as speechPart,
    SP.translate as speechPartTranslate,
    SP.answers_to as answersTo
FROM vocabulary_sentence VS, vocabulary V, speech_part SP
where V.id = VS.vocabulary_id and SP.id = V.speech_part_id and SP.title not ilike any(array[?3]) and VS.default_word != ?2
ORDER BY RANDOM()
LIMIT ?1;
""", nativeQuery = true)
    List<VocabularyDTO> getRandomWordList(Integer size, String ignoreWord, String[] ignoreList);
}
