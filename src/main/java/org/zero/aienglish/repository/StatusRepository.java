package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
