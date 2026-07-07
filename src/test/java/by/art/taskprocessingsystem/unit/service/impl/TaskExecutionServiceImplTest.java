package by.art.taskprocessingsystem.unit.service.impl;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskType;
import by.art.taskprocessingsystem.exception.TaskHandlerNotFoundException;
import by.art.taskprocessingsystem.repository.TaskRepository;
import by.art.taskprocessingsystem.service.TaskHandler;
import by.art.taskprocessingsystem.service.TaskHandlerRegistry;
import by.art.taskprocessingsystem.service.TaskLifecycleService;
import by.art.taskprocessingsystem.service.impl.TaskExecutionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskExecutionServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskHandlerRegistry registry;

    @Mock
    private TaskLifecycleService lifecycleService;

    @InjectMocks
    private TaskExecutionServiceImpl executionService;

    @Test
    void shouldExecuteSuccessfully() {
        UUID taskId = UUID.randomUUID();
        Task task = Task.builder().id(taskId).type(TaskType.EMAIL_NOTIFICATION).payload("{}").build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        TaskHandler handler = mock(TaskHandler.class);
        when(registry.getRequiredHandler(TaskType.EMAIL_NOTIFICATION)).thenReturn(handler);

        executionService.execute(taskId);

        verify(handler).handle("{}");
        verify(lifecycleService).completeTask(taskId);
    }

    @Test
    void shouldFailTaskWhenHandlerThrowsException() {
        UUID taskId = UUID.randomUUID();
        Task task = Task.builder().id(taskId).type(TaskType.EMAIL_NOTIFICATION).build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        TaskHandler handler = mock(TaskHandler.class);
        when(registry.getRequiredHandler(TaskType.EMAIL_NOTIFICATION)).thenReturn(handler);
        doThrow(new RuntimeException("Handler error")).when(handler).handle(any());

        executionService.execute(taskId);

        verify(handler).handle(any());
        verify(lifecycleService).failTask(taskId, "Handler error");
    }

    @Test
    void shouldFailTaskWhenHandlerNotFound() {
        UUID taskId = UUID.randomUUID();
        Task task = Task.builder().id(taskId).type(TaskType.EMAIL_NOTIFICATION).build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(registry.getRequiredHandler(TaskType.EMAIL_NOTIFICATION))
                .thenThrow(new TaskHandlerNotFoundException(TaskType.EMAIL_NOTIFICATION));

        executionService.execute(taskId);

        verify(lifecycleService).failTask(eq(taskId), anyString());
    }
}