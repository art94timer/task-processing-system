package by.art.taskprocessingsystem.mapper;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(CreateTaskRequest request);

    TaskResponse toDto(Task task);
}
