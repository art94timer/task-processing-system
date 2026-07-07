package by.art.taskprocessingsystem.service;

import java.util.List;
import java.util.UUID;

public interface TaskLifecycleService {

    List<UUID> lockTasks(int limit);

    void completeTask(UUID id);

    void failTask(UUID id, String errorMessage);

    void recoverZombieTasks(int minutesThreshold);
}
