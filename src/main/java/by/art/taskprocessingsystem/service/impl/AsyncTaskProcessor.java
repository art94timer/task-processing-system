package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.service.TaskExecutionService;
import by.art.taskprocessingsystem.service.TaskProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AsyncTaskProcessor implements TaskProcessor {

    private final TaskExecutionService executionService;

    @Async("taskExecutor")
    @Override
    public void processTask(UUID taskId) {
        executionService.execute(taskId);
    }
}
