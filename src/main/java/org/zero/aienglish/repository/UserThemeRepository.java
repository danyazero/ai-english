package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.UserThemeEntity;

import java.util.List;

public interface UserThemeRepository extends JpaRepository<UserThemeEntity, Integer> {
    @Modifying
    @Transactional
    @Query("delete from user_theme uf where uf.theme.id in ?1 and uf.user.id = ?2")
    void deleteByThemeIdAndUserId(List<Integer> filmId, Integer userId);
}
