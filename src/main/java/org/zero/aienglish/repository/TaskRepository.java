package org.zero.aienglish.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.zero.aienglish.entity.Task;

public interface TaskRepository extends KeyValueRepository<Task, Integer> {
}
