package by.art.taskprocessingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Formula("""
            CASE priority
              WHEN 'HIGH' then 3
              WHEN 'MEDIUM' then 2
              WHEN 'LOW' then 1
              ELSE 0
            END
            """)
    private int priorityWeight;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskType type = TaskType.EMAIL_NOTIFICATION;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.NEW;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime executeAt;

    @Column(nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private Integer retryCount = 0;

    @CreatedDate
    @Column(updatable = false, nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;


    public void markProcessing() {
        this.status = TaskStatus.PROCESSING;
    }

    public void complete() {
        this.status = TaskStatus.DONE;
    }

    public boolean canRetry(int maxRetries) {
        return retryCount < maxRetries;
    }

    public void scheduleRetry(String reason, LocalDateTime nextExecuteAt) {
        this.retryCount = this.retryCount + 1;
        this.errorMessage = reason;
        this.status = TaskStatus.NEW;
        this.executeAt = nextExecuteAt;
    }

    public void failPermanently(String reason) {
        this.retryCount = this.retryCount + 1;
        this.errorMessage = reason;
        this.status = TaskStatus.FAILED;
    }
}
