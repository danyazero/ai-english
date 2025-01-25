package org.zero.aienglish.model;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
        private String word;
        private String secondForm;
        private String thirdForm;
        private Short order;
        private String defaultWord;
        private String translate;
        private String meaning;
        private Boolean isModal;
        private Boolean isMarker;
}
