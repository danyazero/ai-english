package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SimpleFormConverter implements Function<String, String> {
    @Override
    public String apply(String verb) {
        if (verb.matches(".*(s|ss|sh|ch|x|z)$")) {
            return verb + "es";
        }

        if (verb.matches(".*[^aeiou]y$")) {
            return verb.substring(0, verb.length() - 1) + "ies";
        }

        return verb + "s";
    }
}
