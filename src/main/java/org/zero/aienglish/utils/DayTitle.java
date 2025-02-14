package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DayTitle implements Function<Integer, String> {
    @Override
    public String apply(Integer dayCount) {
        if (dayCount % 10 == 1 && dayCount % 100 != 11) {
            return dayCount + " день";
        } else if ((dayCount % 10 >= 2 && dayCount % 10 <= 4) && (dayCount % 100 < 10 || dayCount % 100 >= 20)) {
            return dayCount + " дні";
        } else {
            return dayCount + " днів";
        }
    }
}
