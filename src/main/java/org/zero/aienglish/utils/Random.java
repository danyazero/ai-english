package org.zero.aienglish.utils;

public class Random {
    public static int nextInRange(int min, int max) {
        var random = new java.util.Random();
        return random.nextInt(max - min) + min;
    }
}
