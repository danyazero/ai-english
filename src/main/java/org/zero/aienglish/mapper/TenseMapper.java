package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.TenseEntity;
import org.zero.aienglish.model.TenseDTO;
import org.zero.aienglish.model.WordResponseDTO;

@Mapper(builder = @Builder(disableBuilder = true))
public interface TenseMapper {
    @Mapping(target = "tense", expression = "java(tense.getTitleTime().getTitle() + \" \" + tense.getTitleDuration().getTitle())")
    TenseDTO map(TenseEntity tense);

    TenseDTO map(org.zero.aienglish.model.Tense tense);

    @Mapping(target = "word", source = "tense")
    @Mapping(target = "translate", ignore = true)
    @Mapping(target = "speechPart",  ignore = true)
    @Mapping(target = "isMarker", ignore = true)
    WordResponseDTO map(TenseDTO tenseDTO);
}
