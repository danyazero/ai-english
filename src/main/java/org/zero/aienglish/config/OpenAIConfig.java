package org.zero.aienglish.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
//@Component
public class OpenAIConfig {
    private String model;
    private String key;
}