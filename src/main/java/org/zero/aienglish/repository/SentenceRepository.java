package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Sentence;

import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    Optional<Sentence> findFirstBySentenceLikeIgnoreCase(String sentence);
}
