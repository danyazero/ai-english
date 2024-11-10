package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.SentenceUserHistoryEntity;

public interface AnswersHistoryRepository extends JpaRepository<SentenceUserHistoryEntity, Integer> {
}
