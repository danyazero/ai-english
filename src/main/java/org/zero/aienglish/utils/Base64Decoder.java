package org.zero.aienglish.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class Base64Decoder implements Function<String, Optional<String>> {
    @Override
    public Optional<String> apply(String originalString) {
        try {
            return Optional.of(new String(Base64.getDecoder().decode(originalString.getBytes())));
        } catch (Exception e) {
            log.warn("Base64 decoding exception");
            return Optional.empty();
        }
    }
}
