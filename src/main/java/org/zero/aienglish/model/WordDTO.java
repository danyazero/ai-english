package org.zero.aienglish.model;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
        private String word;
        private String translate;
        private String speechPart;
}
