package by.art.taskprocessingsystem.service;

import by.art.taskprocessingsystem.entity.TaskType;

public interface TaskHandler {

    void handle(String payload);

    TaskType getType();
}
