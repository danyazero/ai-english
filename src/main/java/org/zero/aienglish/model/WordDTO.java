package org.zero.aienglish.model;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
        private String word;
        private Short order;
        private String sentenceForm;
        private String translate;
        private String speechPart;
        private Boolean isMarker;
}
