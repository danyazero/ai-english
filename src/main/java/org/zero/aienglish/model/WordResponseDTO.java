package org.zero.aienglish.model;

import lombok.*;
import org.zero.aienglish.entity.SpeechPartEntity;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordResponseDTO {
    Integer id;
    String word = "__";
    String translate = "__";
    String speechPart;
    Boolean isMarker = Boolean.FALSE;
}
