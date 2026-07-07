package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Slf4j
public class ZombieTaskProcessor implements TaskProcessor {

    private final TaskRepository repository;
    private final TaskRetryManager retryManager;

    @Override
    @Transactional
    public void processTask(UUID taskId) {
        repository.findById(taskId).ifPresentOrElse(task -> {
            retryManager.recoverFromZombie(task);
            repository.save(task);
        }, () -> log.debug("Zombie task {} no longer exists; skipping", taskId));
    }
}
