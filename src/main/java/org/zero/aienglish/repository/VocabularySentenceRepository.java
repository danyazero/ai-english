package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.VocabularySentence;

public interface VocabularySentenceRepository extends JpaRepository<VocabularySentence, Integer> {
}
