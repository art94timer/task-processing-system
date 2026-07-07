package by.art.taskprocessingsystem.unit.service.impl;

import by.art.taskprocessingsystem.TestData;
import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.mapper.TaskMapper;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.impl.TaskCrudServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskCrudServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Spy
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    @InjectMocks
    private TaskCrudServiceImpl taskService;

    @Test
    void shouldCreateTask() {
        CreateTaskRequest createTaskRequest = TestData.getCreateTaskRequest();
        Task task = TestData.getTask();
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(createTaskRequest);

        verify(taskMapper, times(1)).toEntity(any(CreateTaskRequest.class));
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(any(Task.class));

        assertThat(response).isNotNull();

    }

    @Test
    void shouldReturnTaskWhenExist() {
        Task task = TestData.getTask();
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTask(task.getId());

        verify(taskRepository, times(1)).findById(any(UUID.class));
        verify(taskMapper, times(1)).toDto(any(Task.class));

        assertThat(response).isNotNull();
    }


    @Test
    void shouldThrowExceptionWhenDoesNotExist() {
        Task task = TestData.getTask();
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTask(task.getId()));
    }

    @Test
    void shouldUpdateTask() {
        UpdateTaskRequest updateTaskRequest = TestData.getUpdateTaskRequest();
        Task task = TestData.getTask();
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(task));

        taskService.updateTask(task.getId(), updateTaskRequest);

        verify(taskRepository, times(1)).findById(any(UUID.class));
        verify(taskMapper, times(1)).toDto(any(Task.class));
        assertThat(updateTaskRequest.status()).isEqualTo(task.getStatus());
    }
}