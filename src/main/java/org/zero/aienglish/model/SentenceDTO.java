package org.zero.aienglish.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SentenceDTO(

        @NotNull
        @NotEmpty
        String sentence,
        @NotNull
        @NotEmpty
        String translate,
        @Size(min = 1, max = 100)
        String[] tenseList,
        @NotNull
        @Min(0)
        Integer theme
) {
}
