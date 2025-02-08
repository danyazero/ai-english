package org.zero.aienglish.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
public class SHAEncoder implements BiFunction<String, SHAEncoder.Encryption, Optional<String>> {
    public Optional<String> apply(String originalString, Encryption encryption) {
        try {
            var digest = MessageDigest.getInstance(encryption.getTitle());

            byte[] encodedHash = digest.digest(
                    originalString.getBytes(StandardCharsets.UTF_8));

            return Optional.ofNullable(Base64.getEncoder()
                    .encodeToString(encodedHash));
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
    }

    public enum Encryption {
        SHA256("SHA-256"),
        SHA1("SHA-1");

        private String title;

        Encryption(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
