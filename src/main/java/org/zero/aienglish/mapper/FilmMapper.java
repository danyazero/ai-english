package org.zero.aienglish.mapper;

import org.mapstruct.Mapper;
import org.zero.aienglish.entity.Film;
import org.zero.aienglish.model.FilmDTO;

@Mapper
public interface FilmMapper {
    Film filmDTOToFilm(FilmDTO filmDTO);
}
