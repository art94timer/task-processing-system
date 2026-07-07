package by.art.taskprocessingsystem.exception;

import by.art.taskprocessingsystem.entity.TaskType;

public class TaskHandlerNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "No handler registered for task type: %s";

    public TaskHandlerNotFoundException(TaskType type) {
        super(String.format(MESSAGE_TEMPLATE, type));
    }
}
