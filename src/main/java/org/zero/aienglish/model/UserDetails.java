package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.UserEntity;

import java.util.List;
import java.util.Map;

@Builder
public record UserDetails(
        UserEntity user,
        List<Pair> completedToday
) {
}
