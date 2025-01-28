package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class MergeOmitPairs implements Function<String, String> {

    @Override
    public String apply(String sentence) {
        for (int i = 0; i < 5; i++) {
            var mergedOmit = sentence.replaceAll("__ __", "__");

            if (sentence.equals(mergedOmit)) {
                log.info("After replace omits nothing had changed.");
                return mergedOmit;
            }

            sentence = mergedOmit;
        }

        return sentence;
    }
}
