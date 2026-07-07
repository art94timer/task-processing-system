package by.art.taskprocessingsystem.repository;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query(value = """
        SELECT * FROM tasks
        WHERE status = 'NEW' AND (execute_at IS NULL OR execute_at <= :now)
        ORDER BY priority DESC, created_at ASC
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<Task> findTasksToProcess(@Param("now") LocalDateTime now, @Param("limit") int limit);

    List<Task> findAllByStatusAndUpdatedAtBefore(TaskStatus status, LocalDateTime threshold);
}
