package by.art.taskprocessingsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task priority")
public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}