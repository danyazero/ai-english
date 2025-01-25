package org.zero.aienglish.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.UserVocabulary;

import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Integer> {
  Page<UserVocabulary> getAllByUser_Id(Integer userId, Pageable pageable);

  Optional<UserVocabulary> findFirstByUser_IdAndWord_Id(Integer userId, Integer wordId);

  @Modifying
  @Transactional
  void deleteByWord_IdAndUser_Id(Integer wordId, Integer userId);
  Page<UserVocabulary> findAllByUser_Id(Integer userId, Pageable pageable);
}