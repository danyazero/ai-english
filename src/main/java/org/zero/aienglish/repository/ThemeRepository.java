package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.model.ThemeDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    @Query(value = """
            select f.*, (select count(uf.id) from user_theme uf where uf.theme_id = f.id) as favorite from theme f order by favorite DESC limit 6;
            """, nativeQuery = true)
    List<ThemeDTO> findPopularThemeList();

    @Query("select f from theme f where lower(f.title) like lower(?1)")
    List<Theme> findThemeByTitle(String title);

    @Query(value = "select * from theme t where t.last_update <= now() - interval '24 hours' limit 1;", nativeQuery = true)
    Optional<ThemeDTO> findThemeForUpdate();

    @Modifying
    @Transactional
    @Query(value = "update theme t set t.lastUpdate = ?1 where t.id = ?2")
    void updateLastUpdate(Instant lastUpdate, Integer id);
}
