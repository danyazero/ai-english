package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.model.ThemeDTO;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    @Query(value = """
            select f.*, (select count(uf.id) from user_theme uf where uf.theme_id = f.id) as favorite from theme f order by favorite DESC limit 6;
            """, nativeQuery = true)
    List<ThemeDTO> findPopularThemeList();

    @Query("select f from theme f where lower(f.title) like lower(?1)")
    List<Theme> findThemeByTitle(String title);
}
