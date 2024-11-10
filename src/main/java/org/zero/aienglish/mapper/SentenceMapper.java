package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.SentenceEntity;
import org.zero.aienglish.model.Sentence;

@Mapper(builder = @Builder(disableBuilder = true))
public interface SentenceMapper {

    Sentence map(SentenceEntity sentence);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", defaultValue = "0", ignore = true)
    SentenceEntity map(Sentence sentenceDTO);
}
