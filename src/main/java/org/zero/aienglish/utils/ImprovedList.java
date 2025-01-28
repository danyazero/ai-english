package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


@Slf4j
public class ImprovedList<T> extends ArrayList<T> {
    private final List<T> list;

    private ImprovedList(List<T> list) {
        this.list = list;
    }

    public static <T> ImprovedList<T> of(List<T> list) {
        return new ImprovedList<>(list);
    }

    public Optional<T> getIfTrue(Integer index, Predicate<T> predicate) {
        if (index < 0 || index >= list.size()) return Optional.empty();

        var extracted = list.get(index);
        if (predicate.test(extracted)) return Optional.of(extracted);

        return Optional.empty();
    }

    public Optional<T> find(Predicate<T> filter) {
        return list.stream()
                .filter(filter)
                .findFirst();
    }
}
