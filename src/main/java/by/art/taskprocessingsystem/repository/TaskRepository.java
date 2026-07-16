package by.art.taskprocessingsystem.repository;

import by.art.taskprocessingsystem.entity.Task;
import by.art.taskprocessingsystem.entity.TaskStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query(value = """
            SELECT t FROM Task t
            WHERE t.status = 'NEW' AND (t.executeAt IS NULL OR t.executeAt <= :now)
            ORDER BY t.priorityWeight DESC, t.createdAt ASC
            LIMIT :limit
            """)
    List<Task> findTasksToProcess(@Param("now") LocalDateTime now, @Param("limit") int limit);

    List<Task> findAllByStatusAndUpdatedAtBefore(TaskStatus status, LocalDateTime threshold);
}
