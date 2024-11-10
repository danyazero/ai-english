package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class AccuracyCheck implements BiFunction<String, String, Float> {

    @Override
    public Float apply(String answer, String expected) {
        int maxLength = Math.max(answer.length(), expected.length());

        if (maxLength == 0) {
            return 100.0F;
        }

        int distance = levenshteinDistance(answer, expected);

        return (float) ((1.0 - ((float) distance / maxLength)) * 100);
    }

    private static int levenshteinDistance(String answer, String expected) {
        int[] costs = new int[expected.length() + 1];

        for (int i = 0; i <= answer.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= expected.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (answer.charAt(i - 1) != expected.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[expected.length()] = lastValue;
            }
        }
        return costs[expected.length()];
    }
}
