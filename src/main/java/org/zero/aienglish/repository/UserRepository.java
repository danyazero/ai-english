package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
