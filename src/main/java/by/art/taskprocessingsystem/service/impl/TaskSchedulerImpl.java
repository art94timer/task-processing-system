package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.service.TaskLifecycleService;
import by.art.taskprocessingsystem.service.TaskScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskSchedulerImpl implements TaskScheduler {

    private final TaskLifecycleService taskLifecycleService;
    private final AsyncTaskProcessor asyncTaskProcessor;

    @Value("${task.scheduler.batch-size:10}")
    private int batchSize;

    @Value("${task.recovery.threshold-minutes:10}")
    private int thresholdMinutes;

    @Scheduled(fixedDelayString = "${task.scheduler.delay-ms:5000}")
    @Override
    public void scheduleTasks() {
        log.debug("Polling for tasks to schedule");
        List<UUID> taskIds = taskLifecycleService.lockTasks(batchSize);
        if (!taskIds.isEmpty()) {
            log.info("Locked {} tasks for execution. Submitting to processor", taskIds.size());
            for (UUID taskId : taskIds) {
                asyncTaskProcessor.processTask(taskId);
            }
        }
    }

    @Scheduled(fixedDelayString = "${task.recovery.delay-ms:60000}")
    @Override
    public void recoverZombieTasks() {
        log.info("Checking for hung (zombie) tasks");
        taskLifecycleService.recoverZombieTasks(thresholdMinutes);
    }
}
