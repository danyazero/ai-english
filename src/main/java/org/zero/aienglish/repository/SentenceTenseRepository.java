package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.SentenceTense;

public interface SentenceTenseRepository extends JpaRepository<SentenceTense, Integer> {
}
