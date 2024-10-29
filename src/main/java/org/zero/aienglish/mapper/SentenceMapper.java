package org.zero.aienglish.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.model.SentenceDTO;

@Mapper
public interface SentenceMapper {

    SentenceDTO map(Sentence sentence);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", defaultValue = "0", ignore = true)
    Sentence map(SentenceDTO sentenceDTO);
}
