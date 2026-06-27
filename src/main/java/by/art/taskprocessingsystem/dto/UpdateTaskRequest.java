package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskRequest(
        @NotNull
        TaskStatus status) {
}
