package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.entity.TaskType;
import by.art.taskprocessingsystem.service.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationHandler implements TaskHandler {

    @Override
    public void handle(String payload) {
        log.info("Executing email notification task with payload: {}", payload);
        //TODO implement handler
    }

    @Override
    public TaskType getType() {
        return TaskType.EMAIL_NOTIFICATION;
    }
}
