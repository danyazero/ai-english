package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceUserHistoryEntity;
import org.zero.aienglish.model.PairDTO;

import java.util.Optional;

public interface AnswersHistoryRepository extends JpaRepository<SentenceUserHistoryEntity, Integer> {
}
