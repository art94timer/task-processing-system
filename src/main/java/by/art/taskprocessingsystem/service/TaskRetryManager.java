package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
public class TaskRetryManager {

    private final int maxRetries;
    private final long initialBackoffSeconds;
    private final long zombieRecoveryDelaySeconds;
    private final Clock clock;

    public TaskRetryManager(
            @Value("${task.retry.max-retries:3}") int maxRetries,
            @Value("${task.retry.initial-backoff-seconds:30}") long initialBackoffSeconds,
            @Value("${task.recovery.retry-delay-seconds:10}") long zombieRecoveryDelaySeconds,
            Clock clock) {
        this.maxRetries = maxRetries;
        this.initialBackoffSeconds = initialBackoffSeconds;
        this.zombieRecoveryDelaySeconds = zombieRecoveryDelaySeconds;
        this.clock = clock;
    }

    public void retryAfterFailure(Task task, String reason) {
        if (task.canRetry(maxRetries)) {
            long backoffSeconds = initialBackoffSeconds * (1L << task.getRetryCount());
            LocalDateTime nextExecuteAt = LocalDateTime.now(clock).plusSeconds(backoffSeconds);
            task.scheduleRetry(reason, nextExecuteAt);
            log.info("Task {} failed. Scheduling retry #{} in {} seconds",
                    task.getId(), task.getRetryCount(), backoffSeconds);
        } else {
            task.failPermanently(reason);
            log.warn("Task {} failed. Max retries reached", task.getId());
        }
    }

    public void recoverFromZombie(Task task) {
        String reason = "Zombie task recovered after timeout";
        if (task.canRetry(maxRetries)) {
            LocalDateTime nextExecuteAt = LocalDateTime.now(clock).plusSeconds(zombieRecoveryDelaySeconds);
            task.scheduleRetry(reason, nextExecuteAt);
            log.info("Recovered zombie task {} - rescheduled for retry", task.getId());
        } else {
            task.failPermanently(reason);
            log.warn("Recovered zombie task {} - max retries reached", task.getId());
        }
    }
}
