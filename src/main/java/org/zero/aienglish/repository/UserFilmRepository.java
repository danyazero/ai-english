package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.Film;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.entity.UserFilm;

import java.util.List;
import java.util.Optional;

public interface UserFilmRepository extends JpaRepository<UserFilm, Integer> {
    @Modifying
    @Transactional
    @Query("delete from user_film uf where uf.film.id in ?1 and uf.user.id = ?2")
    void deleteByFilmIdAndUserId(List<Integer> filmId, Integer userId);
}
