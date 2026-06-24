package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;

import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTask(UUID id);

}
