package org.zero.aienglish.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OpenAIConfig {
    @Value("${ai.credentials.model}")
    private String model;
    @Value("${ai.credentials.url}")
    private String url;
    @Value("${ai.credentials.key}")
    private String key;
}