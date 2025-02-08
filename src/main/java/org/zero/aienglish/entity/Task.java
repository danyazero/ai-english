package org.zero.aienglish.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.zero.aienglish.model.TaskAnswer;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Builder
@RedisHash
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable {
    private Integer id;
    private List<String> task;
    private String translate;
    private Integer taskId;
    private String taskType;
    private Integer amountSteps;
    private Integer currentStep;
    private List<TaskAnswer> answers;
    @TimeToLive
    private Integer timeToLive;
}
