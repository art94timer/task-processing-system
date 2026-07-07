package by.art.taskprocessingsystem.integration;

import by.art.taskprocessingsystem.TestData;
import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
}
