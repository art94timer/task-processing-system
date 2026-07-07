package by.art.taskprocessingsystem.unit.service;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.service.TaskRetryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TaskRetryManagerTest {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_SECONDS = 30;
    private static final long ZOMBIE_DELAY_SECONDS = 10;

    private Clock clock;
    private TaskRetryManager retryManager;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-07-05T10:00:00Z"), ZoneId.systemDefault());
        retryManager = new TaskRetryManager(
                MAX_RETRIES,
                INITIAL_BACKOFF_SECONDS,
                ZOMBIE_DELAY_SECONDS,
                clock
        );
    }

    @Test
    void shouldScheduleRetryWithExponentialBackoffWhenCanRetry() {
        Task task = Task.builder()
                .retryCount(0)
                .status(TaskStatus.PROCESSING)
                .build();

        retryManager.retryAfterFailure(task, "Test error");

        assertThat(task.getRetryCount()).isEqualTo(1);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.NEW);
        assertThat(task.getErrorMessage()).isEqualTo("Test error");
        LocalDateTime expectedNext = LocalDateTime.now(clock).plusSeconds(INITIAL_BACKOFF_SECONDS);
        assertThat(task.getExecuteAt()).isEqualTo(expectedNext);
    }

    @Test
    void shouldScheduleRetryWithDoubledBackoffOnSubsequentFailures() {
        Task task = Task.builder()
                .retryCount(1)
                .status(TaskStatus.PROCESSING)
                .build();

        retryManager.retryAfterFailure(task, "Another error");

        assertThat(task.getRetryCount()).isEqualTo(2);
        LocalDateTime expectedNext = LocalDateTime.now(clock).plusSeconds(INITIAL_BACKOFF_SECONDS * 2);
        assertThat(task.getExecuteAt()).isEqualTo(expectedNext);
    }

    @Test
    void shouldMarkFailedWhenMaxRetriesExceeded() {
        Task task = Task.builder()
                .retryCount(MAX_RETRIES) // уже достигнут лимит
                .status(TaskStatus.PROCESSING)
                .build();

        retryManager.retryAfterFailure(task, "Fatal");

        assertThat(task.getRetryCount()).isEqualTo(MAX_RETRIES + 1);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.FAILED);
        assertThat(task.getErrorMessage()).isEqualTo("Fatal");
        assertThat(task.getExecuteAt()).isNull();
    }

    @Test
    void shouldRecoverZombieWithFixedDelayWhenCanRetry() {
        Task task = Task.builder()
                .retryCount(0)
                .status(TaskStatus.PROCESSING)
                .build();

        retryManager.recoverFromZombie(task);

        assertThat(task.getRetryCount()).isEqualTo(1);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.NEW);
        assertThat(task.getErrorMessage()).isEqualTo("Zombie task recovered after timeout");
        LocalDateTime expectedNext = LocalDateTime.now(clock).plusSeconds(ZOMBIE_DELAY_SECONDS);
        assertThat(task.getExecuteAt()).isEqualTo(expectedNext);
    }

    @Test
    void shouldMarkFailedWhenZombieExceedsMaxRetries() {
        Task task = Task.builder()
                .retryCount(MAX_RETRIES)
                .status(TaskStatus.PROCESSING)
                .build();

        retryManager.recoverFromZombie(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.FAILED);
        assertThat(task.getRetryCount()).isEqualTo(MAX_RETRIES + 1);
    }
}