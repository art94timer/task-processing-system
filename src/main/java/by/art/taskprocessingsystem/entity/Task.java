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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
}