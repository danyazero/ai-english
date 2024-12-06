package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.SentenceUserHistory;

public interface AnswersHistoryRepository extends JpaRepository<SentenceUserHistory, Integer> {
}
