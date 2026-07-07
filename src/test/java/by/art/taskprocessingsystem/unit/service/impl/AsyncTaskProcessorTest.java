package by.art.taskprocessingsystem.unit.service.impl;

import by.art.taskprocessingsystem.service.TaskExecutionService;
import by.art.taskprocessingsystem.service.impl.AsyncTaskProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncTaskProcessorTest {

    @Mock
    private TaskExecutionService executionService;

    @InjectMocks
    private AsyncTaskProcessor processor;

    @Test
    void shouldDelegateToExecutionService() {
        UUID taskId = UUID.randomUUID();
        processor.processTask(taskId);
        verify(executionService).execute(taskId);
    }
}