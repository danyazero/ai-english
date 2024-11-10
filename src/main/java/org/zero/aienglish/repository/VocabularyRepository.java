package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.VocabularyEntity;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<VocabularyEntity, Integer> {
    @Query(value = "SELECT * FROM vocabulary f WHERE LOWER(f.word) ILIKE ANY(ARRAY[?1])", nativeQuery = true)
List<VocabularyEntity> findAllByWord(String[] word);
}
