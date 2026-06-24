package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTaskRequest(@NotBlank String name,
                                String description,
                                @NotNull TaskPriority priority,
                                LocalDateTime executeAt) {
}
