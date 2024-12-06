package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.SentenceTense;
import org.zero.aienglish.entity.Tense;

import java.util.List;

public interface SentenceTenseRepository extends JpaRepository<SentenceTense, Integer> {
    List<SentenceTense> findAllBySentenceId(Integer id);

    @Query("select t from SentenceTense s, Tense t where s.sentence = ?1 and t.id = s.tense.id")
    List<Tense> findAllBySentence(Sentence sentence);
}
