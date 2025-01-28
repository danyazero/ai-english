package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MarkCurrentKey implements Function<String, String> {
    private final Pattern omitWithKeyPattern = Pattern.compile("__ \\((\\w+)\\)");
    private final Pattern keyPattern = Pattern.compile("\\((\\w+)\\)");

    @Override
    public String apply(String sentence) {
        var founded = omitWithKeyPattern.matcher(sentence);

        if (founded.find()) {
            log.info("In sentence founded keys");
            var firstFounded = founded.group(0);

            var extracted = keyPattern.matcher(firstFounded);
            if (extracted.find()) {
                log.info("In sentence extracted keys");
                sentence = sentence.replace(firstFounded, "__ <b>" + extracted.group(0) + "</b>");
            }
        }

        return sentence;
    }
}
