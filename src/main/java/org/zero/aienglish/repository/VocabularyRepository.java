package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Vocabulary;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {
    @Query(value = "SELECT * FROM vocabulary f WHERE LOWER(f.word) ILIKE ANY(ARRAY[?1])", nativeQuery = true)
List<Vocabulary> findAllByWord(String[] word);
}
