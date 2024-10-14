package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Sentence;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
}
