package org.zero.aienglish.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.model.PairDTO;

import java.util.List;
import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Integer> {
  List<UserVocabulary> getAllByUser_Id(Integer userId, Pageable pageable);

  Optional<UserVocabulary> findFirstByUser_IdAndWord_Id(Integer userId, Integer wordId);
}