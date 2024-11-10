package org.zero.aienglish.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Sentence(

        @NotNull
        @NotEmpty
        String sentence,
        @NotNull
        @NotEmpty
        String translation,
        @Size(min = 1, max = 100)
        String[] sentenceTense,
        @NotNull
        @NotEmpty
        List<WordDTO> vocabulary
) {
}
