package org.zero.aienglish.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.zero.aienglish.model.ThemeDTO;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@Builder
@RedisHash("recommendation_state")
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationState implements Serializable {
    private Integer id;
    private Integer step;
    private Integer currenThemeId;
    private Integer currentThemeIndex;
    private List<ThemeDTO> selectedThemes;
    private List<Integer> todayThemes;
    @TimeToLive
    private Integer timeToLive;
}
