package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Pagination<T>(
        List<T> items,
        Integer totalPages,
        Integer currentPage
) {
}
