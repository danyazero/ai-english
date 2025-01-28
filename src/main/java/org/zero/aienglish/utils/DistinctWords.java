package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;
import org.zero.aienglish.model.TaskAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DistinctWords implements Function<List<TaskAnswer>, List<TaskAnswer>> {
    @Override
    public List<TaskAnswer> apply(List<TaskAnswer> wordResponseDTOS) {
        return new ArrayList<>(wordResponseDTOS.stream().collect(Collectors.toMap(
                        TaskAnswer::word,
                        obj -> obj,
                        (existing, replacement) -> existing))
                .values());
    }
}
