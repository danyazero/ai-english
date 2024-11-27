package org.zero.aienglish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@CrossOrigin(origins = {"http://wp8890.ddns.mksat.net"}, allowCredentials = "true")
@SpringBootApplication
public class AiEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiEnglishApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
