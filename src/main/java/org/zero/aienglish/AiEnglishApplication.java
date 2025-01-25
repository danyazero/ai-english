package org.zero.aienglish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableCaching
@SpringBootApplication
public class AiEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiEnglishApplication.class, args);
    }
}
