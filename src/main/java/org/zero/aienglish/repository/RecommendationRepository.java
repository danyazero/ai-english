package org.zero.aienglish.repository;

import org.springframework.data.repository.CrudRepository;
import org.zero.aienglish.entity.RecommendationState;

public interface RecommendationRepository extends CrudRepository<RecommendationState, Integer> {
}
