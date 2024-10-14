package org.zero.aienglish.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.WordDTO;

@Mapper
public interface WordMapper {

    @Mapping(target = "speechPart", ignore = true)
    Vocabulary map(WordDTO wordDTO);
    @Mapping(target = "speechPart", ignore = true)
    WordDTO map(Vocabulary word);
}
