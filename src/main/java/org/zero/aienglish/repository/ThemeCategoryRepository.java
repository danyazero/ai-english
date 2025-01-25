package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.ThemeCategory;

public interface ThemeCategoryRepository extends JpaRepository<ThemeCategory, Integer> {
}
