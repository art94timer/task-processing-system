package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskExecutionService;
import by.art.taskprocessingsystem.service.TaskHandler;
import by.art.taskprocessingsystem.service.TaskHandlerRegistry;
import by.art.taskprocessingsystem.service.TaskLifecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final TaskRepository taskRepository;
    private final TaskHandlerRegistry registry;
    private final TaskLifecycleService lifecycleService;

    @Override
    public void execute(UUID taskId) {
        log.info("Starting processing task: {}", taskId);
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));

            TaskHandler handler = registry.getRequiredHandler(task.getType());
            handler.handle(task.getPayload());

            lifecycleService.completeTask(taskId);
        } catch (Exception e) {
            log.error("Error processing task: {}", taskId, e);
            lifecycleService.failTask(taskId, e.getMessage());
        }
    }
}
