package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.mapper.TaskMapper;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = mapper.toEntity(request);
        task = repository.save(task);
        return mapper.toDto(task);
    }

    @Override
    public TaskResponse getTask(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
