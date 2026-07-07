package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskLifecycleService;
import by.art.taskprocessingsystem.service.TaskRetryManager;
import by.art.taskprocessingsystem.service.ZombieTaskProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLifecycleServiceImpl implements TaskLifecycleService {

    private final TaskRepository repository;
    private final TaskRetryManager retryManager;
    private final Clock clock;
    private final ZombieTaskProcessor zombieTaskProcessor;

    @Override
    @Transactional
    public List<UUID> lockTasks(int limit) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Task> tasks = repository.findTasksToProcess(now, limit);
        if (tasks.isEmpty()) {
            return List.of();
        }
        for (Task task : tasks) {
            task.markProcessing();
        }
        repository.saveAll(tasks);
        return tasks.stream().map(Task::getId).toList();
    }

    @Override
    @Transactional
    public void completeTask(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.complete();
        repository.save(task);
        log.info("Task {} completed successfully", id);
    }

    @Override
    @Transactional
    public void failTask(UUID id, String errorMessage) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        retryManager.retryAfterFailure(task, errorMessage);
        repository.save(task);

    }

    @Override
    public void recoverZombieTasks(int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now(clock).minusMinutes(minutesThreshold);
        List<UUID> zombieTaskIds = repository
                .findAllByStatusAndUpdatedAtBefore(TaskStatus.PROCESSING, threshold)
                .stream()
                .map(Task::getId)
                .toList();

        if (zombieTaskIds.isEmpty()) {
            return;
        }

        log.info("Found {} zombie tasks to recover", zombieTaskIds.size());
        for (UUID taskId : zombieTaskIds) {
            try {
                zombieTaskProcessor.processTask(taskId);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Task {} was concurrently updated during zombie recovery; skipping", taskId);
            }
        }
    }
}
