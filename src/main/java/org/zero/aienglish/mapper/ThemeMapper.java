package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.zero.aienglish.entity.ThemeEntity;
import org.zero.aienglish.model.ThemeDTO;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ThemeMapper {
    ThemeEntity map(ThemeDTO themeDTO);
}
