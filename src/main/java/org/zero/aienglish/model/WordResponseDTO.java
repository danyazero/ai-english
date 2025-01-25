package org.zero.aienglish.model;

import lombok.*;

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
    Integer order = 0;

    public WordResponseDTO getCopyWithOtherWord(String otherWord) {
        return WordResponseDTO.builder()
                .id(this.id)
                .word(otherWord)
                .translate(this.translate)
                .order(this.order)
                .build();
    }
}
