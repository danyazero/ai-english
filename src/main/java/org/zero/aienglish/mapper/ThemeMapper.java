package org.zero.aienglish.mapper;

import org.mapstruct.Mapper;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.model.ThemeDTO;

@Mapper
public interface ThemeMapper {
    Theme map(ThemeDTO themeDTO);
}
