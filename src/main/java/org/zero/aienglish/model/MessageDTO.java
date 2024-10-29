package org.zero.aienglish.model;

import java.util.List;

public record MessageDTO<T>(
        String role,
        T content
) {
}
