package by.art.taskprocessingsystem.unit.service.impl;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskRetryManager;
import by.art.taskprocessingsystem.service.ZombieTaskProcessor;
import by.art.taskprocessingsystem.service.impl.TaskLifecycleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskLifecycleServiceImplTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskRetryManager retryManager;

    @Mock
    private ZombieTaskProcessor zombieTaskProcessor;

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-07-05T10:00:00Z"), ZoneId.systemDefault());

    @InjectMocks
    private TaskLifecycleServiceImpl service;


    @Test
    void shouldLockTasksAndMarkProcessing() {
        LocalDateTime now = LocalDateTime.now(clock);
        Task task1 = Task.builder().id(UUID.randomUUID()).status(TaskStatus.NEW).build();
        Task task2 = Task.builder().id(UUID.randomUUID()).status(TaskStatus.NEW).build();
        when(repository.findTasksToProcess(now, 10)).thenReturn(List.of(task1, task2));

        List<UUID> lockedIds = service.lockTasks(10);

        verify(repository).findTasksToProcess(now, 10);
        verify(repository).saveAll(List.of(task1, task2));
        assertThat(lockedIds).containsExactly(task1.getId(), task2.getId());
        assertThat(task1.getStatus()).isEqualTo(TaskStatus.PROCESSING);
        assertThat(task2.getStatus()).isEqualTo(TaskStatus.PROCESSING);
    }

    @Test
    void shouldCompleteTask() {
        UUID id = UUID.randomUUID();
        Task task = Task.builder().id(id).status(TaskStatus.PROCESSING).build();
        when(repository.findById(id)).thenReturn(Optional.of(task));

        service.completeTask(id);

        verify(repository).save(task);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void shouldFailTaskAndDelegateToRetryManager() {
        UUID id = UUID.randomUUID();
        Task task = Task.builder().id(id).status(TaskStatus.PROCESSING).build();
        when(repository.findById(id)).thenReturn(Optional.of(task));

        service.failTask(id, "Something went wrong");

        verify(retryManager).retryAfterFailure(task, "Something went wrong");
        verify(repository).save(task);
    }

    @Test
    void shouldRecoverZombieTasks() {
        LocalDateTime threshold = LocalDateTime.now(clock).minusMinutes(10);
        Task zombie1 = Task.builder().id(UUID.randomUUID()).status(TaskStatus.PROCESSING).build();
        Task zombie2 = Task.builder().id(UUID.randomUUID()).status(TaskStatus.PROCESSING).build();
        when(repository.findAllByStatusAndUpdatedAtBefore(TaskStatus.PROCESSING, threshold))
                .thenReturn(List.of(zombie1, zombie2));

        service.recoverZombieTasks(10);

        verify(repository).findAllByStatusAndUpdatedAtBefore(TaskStatus.PROCESSING, threshold);
        verify(zombieTaskProcessor, times(2)).processTask(any(UUID.class));
    }

    @Test
    void shouldDoNothingWhenNoZombies() {
        when(repository.findAllByStatusAndUpdatedAtBefore(any(), any())).thenReturn(List.of());

        service.recoverZombieTasks(10);

        verify(repository, never()).saveAll(any());
        verify(retryManager, never()).recoverFromZombie(any());
    }
}
