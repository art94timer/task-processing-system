package by.art.taskprocessingsystem.integration;

import by.art.taskprocessingsystem.TestData;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskPriority;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryIT extends AbstractIT {

    @Autowired
    private TaskRepository repository;


    @Test
    void shouldSaveTask() {
        Task task = TestData.getTask().toBuilder()
                .id(null)
                .build();
        Task saved = repository.save(task);
        Optional<Task> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    void shouldOrderTasksByPriorityThenCreationTime() {
        LocalDateTime now = LocalDateTime.now();

        Task low = TestData.getTask().toBuilder()
                .id(null)
                .priority(TaskPriority.LOW)
                .status(TaskStatus.NEW)
                .executeAt(now.minusMinutes(1))
                .build();
        Task medium = TestData.getTask().toBuilder()
                .id(null)
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.NEW)
                .executeAt(now.minusMinutes(1))
                .build();
        Task high = TestData.getTask().toBuilder()
                .id(null)
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.NEW)
                .executeAt(now.minusMinutes(1))
                .build();

        repository.saveAll(List.of(low, medium, high));

        List<Task> result = repository.findTasksToProcess(now, 10);

        assertThat(result)
                .extracting(Task::getPriority)
                .containsExactly(TaskPriority.HIGH, TaskPriority.MEDIUM, TaskPriority.LOW);
    }

}
