package by.art.taskprocessingsystem.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Task not found with id: %s";

    public TaskNotFoundException(UUID id) {
        super(String.format(MESSAGE_TEMPLATE, id));
    }
}
