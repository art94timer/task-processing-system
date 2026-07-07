package by.art.taskprocessingsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task type")
public enum TaskType {
    EMAIL_NOTIFICATION,
    DATA_CLEANUP,
    GENERATE_REPORT
}
