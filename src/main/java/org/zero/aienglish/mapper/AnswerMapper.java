package org.zero.aienglish.mapper;

import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Task;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskType;

@Component
public class AnswerMapper {
    public static TaskResultDTO map(Task task) {
        return TaskResultDTO.builder()
                .taskId(task.getTaskId())
                .taskType(TaskType.valueOf(task.getTaskType()))
                .answer(task.getTask().getLast())
                .build();
    }

}
