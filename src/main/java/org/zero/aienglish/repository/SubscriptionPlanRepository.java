package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    @Query("select s from SubscriptionPlan s where s.price > 0")
    List<SubscriptionPlan> findAllPaidPlans();

    SubscriptionPlan findFirstByNameIgnoreCase(String name);
}
