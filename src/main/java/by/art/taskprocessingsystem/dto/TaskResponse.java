package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

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
                description = "Task status",
                example = "NEW"
        )
        TaskStatus status,

        @Schema(
                description = "Scheduled execution time",
                example = "2026-06-30T10:00:00"
        )
        LocalDateTime executeAt
        ) { }
