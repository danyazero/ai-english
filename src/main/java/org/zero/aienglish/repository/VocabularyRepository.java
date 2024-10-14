package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Vocabulary;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {
}
