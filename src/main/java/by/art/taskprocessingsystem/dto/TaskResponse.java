package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.entity.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record TaskResponse(
        @Schema(
                description = "Task identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Task name",
                example = "Send email notification"
        )
        String name,

        @Schema(
                description = "Task description",
                example = "Notify user about completed order"
        )
        String description,

        @Schema(
                description = "Task priority",
                example = "HIGH"
        )
        TaskPriority priority,

        @Schema(
                description = "Task type",
                example = "EMAIL_NOTIFICATION"
        )
        TaskType type,

        @Schema(
                description = "Task payload",
                example = "{\"email\": \"user@example.com\"}"
        )
        String payload,

        @Schema(
                description = "Task status",
                example = "NEW"
        )
        TaskStatus status,

        @Schema(
                description = "Error message of last failure",
                example = "Connection timed out"
        )
        String errorMessage,

        @Schema(
                description = "Scheduled execution time",
                example = "2026-06-30T10:00:00"
        )
        LocalDateTime executeAt
) {
}
