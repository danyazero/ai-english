package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.zero.aienglish.model.WordResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DistinctWords implements Function<List<WordResponseDTO>, List<WordResponseDTO>> {
    @Override
    public List<WordResponseDTO> apply(List<WordResponseDTO> wordResponseDTOS) {
        return new ArrayList<>(wordResponseDTOS.stream().collect(Collectors.toMap(
                        WordResponseDTO::getWord,
                        obj -> obj,
                        (existing, replacement) -> existing))
                .values());
    }
}
