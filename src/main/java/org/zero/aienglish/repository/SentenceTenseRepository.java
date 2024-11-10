package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SentenceEntity;
import org.zero.aienglish.entity.SentenceTenseEntity;
import org.zero.aienglish.entity.TenseEntity;

import java.util.List;

public interface SentenceTenseRepository extends JpaRepository<SentenceTenseEntity, Integer> {
    List<SentenceTenseEntity> findAllBySentenceId(Integer id);

    @Query("select t from SentenceTenseEntity s, TenseEntity t where s.sentence = ?1 and t.id = s.tense.id")
    List<TenseEntity> findAllBySentence(SentenceEntity sentence);
}
