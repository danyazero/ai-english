package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceHistory;

import java.util.Optional;

public interface SentenceHistoryRepository extends JpaRepository<SentenceHistory, Integer> {
    Optional<SentenceHistory> findFirstByUser_IdAndStatus_IdOrderByAtDesc(Integer userId, Integer status);
    Optional<SentenceHistory> findByUser_IdOrderByAtAsc(Integer userId);

}
