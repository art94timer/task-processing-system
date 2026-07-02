package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateTaskRequest(
        @NotNull
        TaskStatus status) {
}
