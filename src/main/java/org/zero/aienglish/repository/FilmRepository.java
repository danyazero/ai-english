package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.aienglish.entity.Film;
import org.zero.aienglish.model.FilmDTO;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Integer> {
    @Query(value = """
select f.*, (select count(uf.id) from user_film uf where uf.film_id = f.id) as favorite from film f order by favorite DESC limit 6;
""", nativeQuery = true)
    List<FilmDTO> findPopularFilms();
    @Query("select f from film f where lower(f.title) like lower(?1)")
    List<Film> findFilmByTitle(String title);
}
