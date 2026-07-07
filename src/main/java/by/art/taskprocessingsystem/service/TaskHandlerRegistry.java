package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.entity.TaskType;
import by.art.taskprocessingsystem.exception.TaskHandlerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TaskHandlerRegistry {

    private final Map<TaskType, TaskHandler> handlers;

    public TaskHandlerRegistry(List<TaskHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(TaskHandler::getType, Function.identity()));
    }

    public Optional<TaskHandler> getHandler(TaskType type) {
        return Optional.ofNullable(handlers.get(type));
    }

    public TaskHandler getRequiredHandler(TaskType type) {
        return Optional.ofNullable(handlers.get(type))
                .orElseThrow(() -> new TaskHandlerNotFoundException(type));
    }
}
