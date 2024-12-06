package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.User;

import java.util.List;

@Builder
public record UserDetails(
        User user,
        List<Pair> completedToday
) {
}
