package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.SentenceHistory;

public interface SentenceHistoryRepository extends JpaRepository<SentenceHistory, Integer> {
}
