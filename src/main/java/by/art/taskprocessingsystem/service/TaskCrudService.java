package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.PageResponse;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskCrudService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTask(UUID id);

    PageResponse<TaskResponse> getTasks(Pageable pageable);

    TaskResponse updateTask(UUID id, UpdateTaskRequest request);
}
