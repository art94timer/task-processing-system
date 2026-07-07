package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.PageResponse;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.mapper.TaskMapper;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCrudServiceImpl implements TaskCrudService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = mapper.toEntity(request);
        task = repository.save(task);
        return mapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getTasks(Pageable pageable) {
        Page<Task> taskPage = repository.findAll(pageable);
        return mapper.toPageResponse(taskPage);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        mapper.updateTask(request, task);
        return mapper.toDto(task);
    }
}
