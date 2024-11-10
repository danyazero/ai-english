package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SpeechPartEntity;

import java.util.List;

public interface SpeechRepository extends JpaRepository<SpeechPartEntity, Integer> {
    @Query(value = "SELECT * FROM speech_part f WHERE LOWER(f.title) ILIKE ANY(ARRAY[?1])", nativeQuery = true)
    List<SpeechPartEntity> findByTitle(String[] title);
}
