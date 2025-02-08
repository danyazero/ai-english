package org.zero.aienglish.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Deserializer {
    public static<T> T apply(String jsonObject, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        T object = null;
        try {
            object = objectMapper.readValue(jsonObject, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
