package by.art.taskprocessingsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task status")
public enum TaskStatus {
    NEW,
    PROCESSING,
    DONE,
    FAILED
}
