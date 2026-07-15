package by.art.taskprocessingsystem.unit.service;

import by.art.taskprocessingsystem.entity.TaskType;
import by.art.taskprocessingsystem.exception.TaskHandlerNotFoundException;
import by.art.taskprocessingsystem.service.TaskHandler;
import by.art.taskprocessingsystem.service.TaskHandlerRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskHandlerRegistryTest {

    @Test
    void shouldReturnHandlerWhenExists() {
        TaskHandler handler = mock(TaskHandler.class);
        when(handler.getType()).thenReturn(TaskType.EMAIL_NOTIFICATION);
        TaskHandlerRegistry registry = new TaskHandlerRegistry(List.of(handler));

        Optional<TaskHandler> result = registry.getHandler(TaskType.EMAIL_NOTIFICATION);
        assertThat(result).contains(handler);
    }

    @Test
    void shouldReturnEmptyOptionalWhenHandlerNotFound() {
        TaskHandlerRegistry registry = new TaskHandlerRegistry(List.of());
        Optional<TaskHandler> result = registry.getHandler(TaskType.WEBHOOK_DELIVERY);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnRequiredHandlerWhenExists() {
        TaskHandler handler = mock(TaskHandler.class);
        when(handler.getType()).thenReturn(TaskType.GENERATE_REPORT);
        TaskHandlerRegistry registry = new TaskHandlerRegistry(List.of(handler));

        TaskHandler found = registry.getRequiredHandler(TaskType.GENERATE_REPORT);
        assertThat(found).isSameAs(handler);
    }

    @Test
    void shouldThrowTaskHandlerNotFoundExceptionWhenRequiredHandlerMissing() {
        TaskHandlerRegistry registry = new TaskHandlerRegistry(List.of());
        assertThatThrownBy(() -> registry.getRequiredHandler(TaskType.EMAIL_NOTIFICATION))
                .isInstanceOf(TaskHandlerNotFoundException.class)
                .hasMessageContaining("No handler registered for task type: EMAIL_NOTIFICATION");
    }
}