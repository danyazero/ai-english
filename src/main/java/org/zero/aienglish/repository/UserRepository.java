package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
