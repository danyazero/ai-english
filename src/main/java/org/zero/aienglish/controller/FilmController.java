package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.entity.Film;
import org.zero.aienglish.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/film")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getPopularFilmsList() {
        return filmService.getPopularFilmList();
    }

    @GetMapping("/{title}")
    public List<Film> getFilmByTitle(@PathVariable String title) {
        return filmService.getFilmByTitle(title);
    }

    @PostMapping
    public void addFilmsToUserFavorite(@RequestBody List<Integer> filmList, @RequestHeader("userId") Integer userId) {
        filmService.addFilmToFavorite(filmList, userId);
    }

    @DeleteMapping
    public void deleteFilmsFromUserFavorite(@RequestBody List<Integer> filmList, @RequestHeader("userId") Integer userId) {
        filmService.deleteFilmFromFavorite(filmList, userId);
    }

}
