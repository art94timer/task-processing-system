package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record CreateTaskRequest(
        @Schema(
                description = "Task name",
                example = "Send email notification"
        )
        @NotBlank String name,

        @Schema(
                description = "Task description",
                example = "Notify user about completed order"
        )
        String description,

        @Schema(
                description = "Task priority",
                example = "HIGH"
        )
        @NotNull TaskPriority priority,

        @Schema(
                description = "Task type",
                example = "EMAIL_NOTIFICATION"
        )
        @NotNull TaskType type,

        @Schema(
                description = "Task payload",
                example = "{\"email\": \"user@example.com\"}"
        )
        String payload,

        @Schema(
                description = "Scheduled execution time",
                example = "2026-06-30T10:00:00"
        )
        LocalDateTime executeAt) {
}
