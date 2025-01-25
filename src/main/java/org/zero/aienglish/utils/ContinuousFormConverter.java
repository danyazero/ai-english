package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class ContinuousFormConverter implements Function<String, String> {
    @Override
    public String apply(String input) {
        if (shouldIgnore(input) || isSilentException(input))
            return appendSuffix(input);

        else if (isIE(input))
            return fromStart(input, 2) + "ying";

        char lastChar = input.charAt(input.length() - 1);
        switch (lastChar) {
            case 'e' -> {
                return appendSuffix(fromStart(input, 1));
            }
            case 'c' -> {
                return appendSuffix(input, "king");
            }
        }

        if (isConsonantVowelConsonant(input))
            return getVowelFormatted(input);


        return input + "ing";
    }

    private static boolean isIE(String input) {
        return last(input, 2).equalsIgnoreCase("ie");
    }

    private static boolean shouldIgnore(String input) {
        return List.of("see", "be").contains(input);
    }

    private static String getVowelFormatted(String input) {
        var array = Stream.of("w", "x", "y");
        if (array.anyMatch(input::endsWith)) return input + "ing";

        var lastChar = input.charAt(input.length() - 1);

        return isSyllableStressed(input) ? input + "ing" : input + lastChar + "ing";
    }

    public static boolean isSyllableStressed(String input) {
        return Stream.of("en", "el", "er")
                .anyMatch(input::endsWith);
    }

    private static boolean isSilentException(String input) {
        return Stream.of("ee", "oe", "ye")
                .anyMatch(input::endsWith);
    }

    private static boolean isVowel(char c) {
        return "aeiouAEIOU".indexOf(c) != -1;
    }

    private static boolean isConsonantVowelConsonant(String word) {
        int length = word.length();
        if (length < 3) {
            return false;
        }

        char last = word.charAt(length - 1);
        char middle = word.charAt(length - 2);
        char first = word.charAt(length - 3);

        return !isVowel(last) && isVowel(middle) && !isVowel(first) &&
                !Character.isUpperCase(last);
    }

    private static String appendSuffix(String input) {
        return appendSuffix(input, "ing");
    }

    private static String appendSuffix(String input, String suffix) {
        return input + suffix;
    }

    private static String last(String input, Integer start) {
        return input.substring(input.length() - start);
    }

    private static String fromStart(String input, Integer fromEnd) {
        return input.substring(0, input.length() - fromEnd);
    }
}
