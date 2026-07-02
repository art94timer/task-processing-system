package by.art.taskprocessingsystem;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestData {

    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(
            2026,
            1,
            1,
            1,
            0,
            0
    );
    public static final String TASK_NAME = "Task";
    public static final String TASK_DESCRIPTION = "Send email";

    private TestData() {

    }

    public static CreateTaskRequest getCreateTaskRequest() {
        return CreateTaskRequest.builder()
                .name(TASK_NAME)
                .description(TASK_DESCRIPTION)
                .priority(TaskPriority.HIGH)
                .executeAt(LOCAL_DATE_TIME)
                .build();
    }


    public static CreateTaskRequest getInvalidCreateTaskRequest() {
        return CreateTaskRequest.builder().build();
    }

    public static UpdateTaskRequest getUpdateTaskRequest() {
        return UpdateTaskRequest.builder()
                .status(TaskStatus.DONE)
                .build();
    }

    public static UpdateTaskRequest getInvalidUpdateTaskRequest() {
        return UpdateTaskRequest.builder().build();
    }

    public static Task getTask() {
        return Task.builder()
                .id(UUID.randomUUID())
                .name(TASK_NAME)
                .description(TASK_DESCRIPTION)
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.NEW)
                .createdAt(LOCAL_DATE_TIME)
                .updatedAt(LOCAL_DATE_TIME)
                .retryCount(0)
                .executeAt(LOCAL_DATE_TIME)
                .build();
    }

    public static TaskResponse getTaskResponse() {
        return TaskResponse.builder()
                .id(UUID.randomUUID())
                .name(TASK_NAME)
                .description(TASK_DESCRIPTION)
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.NEW)
                .executeAt(LOCAL_DATE_TIME)
                .build();
    }
}
