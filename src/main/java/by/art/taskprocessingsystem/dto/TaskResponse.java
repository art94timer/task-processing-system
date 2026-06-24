package by.art.taskprocessingsystem.dto;

import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskStatus;

import java.util.UUID;

public record TaskResponse(UUID id,
                           String name,
                           String description,
                           TaskPriority priority,
                           TaskStatus status) {
}
