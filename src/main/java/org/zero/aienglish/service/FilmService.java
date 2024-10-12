package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.Film;
import org.zero.aienglish.entity.UserFilm;
import org.zero.aienglish.mapper.FilmMapper;
import org.zero.aienglish.repository.FilmRepository;
import org.zero.aienglish.repository.UserFilmRepository;
import org.zero.aienglish.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final UserFilmRepository userFilmRepository;
    private final FilmMapper filmMapper;

    public List<Film> getPopularFilmList() {
        var popularFilms = filmRepository.findPopularFilms();

        return popularFilms.stream().map(filmMapper::filmDTOToFilm).toList();
    }

    public List<Film> getFilmByTitle(String title) {
        var searchValue = "%" + String.join( "%", title.split("")) + "%";
        return filmRepository.findFilmByTitle(searchValue);
    }

    public void addFilmToFavorite(List<Integer> filmIdList, Integer userId) {
        var user = userRepository.getReferenceById(userId);
        var userFavoriteFilmList = filmIdList.stream()
                .map(filmRepository::getReferenceById)
                .map(film -> new UserFilm(film, user))
                .toList();
        userFilmRepository.saveAll(userFavoriteFilmList);
    }

    public void deleteFilmFromFavorite(List<Integer> filmList, Integer userId) {
        userFilmRepository.deleteByFilmIdAndUserId(filmList, userId);
    }
}
