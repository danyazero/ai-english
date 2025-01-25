package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.ThemeTheme;

import java.util.List;

public interface ThemeRelyRepository extends JpaRepository<ThemeTheme, Integer> {
    List<ThemeTheme> findAllByTheme_IdOrRely_Id(Integer themeId, Integer relyId);
}
