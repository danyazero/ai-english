package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Status;
import org.zero.aienglish.entity.VocabularyHistory;

import java.util.Optional;

public interface VocabularyHistoryRepository extends JpaRepository<VocabularyHistory, Long> {
    Optional<VocabularyHistory> findFirstByStatus_IdAndUser_Id(Integer status, Integer userId);

    Optional<VocabularyHistory> findFirstByUser_IdAndVocabulary_Id(Integer userId, Integer vocabularyId);
}
