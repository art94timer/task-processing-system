package by.art.taskprocessingsystem.mapper;

import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.PageResponse;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(CreateTaskRequest request);

    TaskResponse toDto(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTask(UpdateTaskRequest request,
                    @MappingTarget Task task);

    default PageResponse<TaskResponse> toPageResponse(Page<Task> page) {
        return new PageResponse<>(
                page.getContent().stream()
                        .map(this::toDto)
                        .toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }
}
