package by.art.taskprocessingsystem.service;

import java.util.UUID;

public interface TaskExecutionService {

    void execute(UUID taskId);
}
