# Task Processing System

A Spring Boot service for creating, scheduling, and asynchronously processing background tasks. Tasks are persisted in
PostgreSQL, picked up by a scheduler, executed by pluggable handlers, and automatically retried with exponential backoff
on failure. Stuck ("zombie") tasks are detected and recovered.

## Features

- **Task CRUD API** — create tasks, fetch by id, list with pagination/sorting, update status
- **Reliable scheduling** — polls for due tasks using `SELECT ... FOR UPDATE SKIP LOCKED`, safe for multiple instances
- **Async execution** — tasks are dispatched to a virtual-thread executor for non-blocking processing
- **Pluggable task handlers** — one handler per `TaskType`, resolved via a registry
- **Automatic retries** — exponential backoff, configurable max attempts
- **Zombie recovery** — tasks stuck in `PROCESSING` past a threshold are detected and rescheduled
- **Optimistic locking** — `@Version` column guards against concurrent updates
- **OpenAPI/Swagger docs** — interactive API documentation
- **Flyway migrations** — versioned schema management
- **Dockerized PostgreSQL** for local development, Testcontainers for integration tests

## Tech Stack

| Layer        | Technology                                    |
|--------------|-----------------------------------------------|
| Language     | Java 21                                       |
| Framework    | Spring Boot 3.5.x (Web, Data JPA, Validation) |
| Database     | PostgreSQL 17                                 |
| Migrations   | Flyway                                        |
| Mapping      | MapStruct                                     |
| API docs     | springdoc-openapi                             |
| Testing      | JUnit 5, Mockito, AssertJ, Testcontainers     |
| Build        | Maven                                         |
| Code quality | Checkstyle                                    |

## Architecture Overview

```
Client
  │
  ▼
TaskController ──► TaskCrudService ──► TaskRepository ──► PostgreSQL
                                                             ▲
TaskScheduler (polls on a fixed delay)                      │
  │  locks NEW/due tasks (SKIP LOCKED)                       │
  ▼                                                          │
TaskLifecycleService ──────────────────────────────────────►┘
  │
  ▼
AsyncTaskProcessor (virtual threads) ──► TaskExecutionService
                                             │
                                             ▼
                                     TaskHandlerRegistry
                                             │
                             ┌───────────────┼───────────────┐
                             ▼               ▼               ▼
                   EmailNotificationHandler DataCleanupHandler GenerateReportHandler

TaskScheduler also runs a periodic zombie sweep:
recoverZombieTasks() → finds PROCESSING tasks stale beyond a threshold
                     → ZombieTaskProcessor → TaskRetryManager
```

On failure or zombie recovery, `TaskRetryManager` either reschedules the task with an exponential backoff delay or marks
it `FAILED` once the retry limit is exceeded.

## Task Lifecycle

```
NEW ──lockTasks()──► PROCESSING ──success──► DONE
                          │
                          ├──failure & retries left──► NEW (with backoff delay)
                          │
                          └──failure & retries exhausted──► FAILED
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (for PostgreSQL and integration tests)

### Run PostgreSQL

```bash
docker-compose up -d
```

This starts PostgreSQL 17 on `localhost:5432` with database `task_processing` (user/password: `postgres`/`postgres`).

### Run the application

```bash
./mvnw spring-boot:run
```

Flyway will run migrations automatically on startup.

### API Documentation

Once running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

### Build & Test

```bash
./mvnw clean verify
```

This runs unit tests, integration tests (via Testcontainers), and Checkstyle validation.

## API Reference

Base path: `/api/v1/tasks`

| Method | Path    | Description                      |
|--------|---------|----------------------------------|
| POST   | `/`     | Create a new task                |
| GET    | `/{id}` | Get a task by id                 |
| GET    | `/`     | List tasks (paginated, sortable) |
| PATCH  | `/{id}` | Update a task's status           |

### Create a task

```http
POST /api/v1/tasks
Content-Type: application/json

{
  "name": "Send email notification",
  "description": "Notify user about completed order",
  "priority": "HIGH",
  "type": "EMAIL_NOTIFICATION",
  "payload": "{\"email\": \"user@example.com\"}",
  "executeAt": "2026-06-30T10:00:00"
}
```

### Task types

- `EMAIL_NOTIFICATION`
- `DATA_CLEANUP`
- `GENERATE_REPORT`

### Task priorities

- `LOW`, `MEDIUM`, `HIGH`

### Task statuses

- `NEW`, `PROCESSING`, `DONE`, `FAILED`

## Configuration

Key properties (`application.properties`):

| Property                             | Default                                            | Description                                                           |
|--------------------------------------|----------------------------------------------------|-----------------------------------------------------------------------|
| `api.base.url`                       | `/api/v1`                                          | API base path                                                         |
| `spring.datasource.url`              | `jdbc:postgresql://localhost:5432/task_processing` | DB connection                                                         |
| `task.scheduler.batch-size`          | `10`                                               | Max tasks locked per scheduling cycle                                 |
| `task.scheduler.delay-ms`            | `5000`                                             | Delay between scheduling polls                                        |
| `task.recovery.delay-ms`             | `60000`                                            | Delay between zombie-recovery sweeps                                  |
| `task.recovery.threshold-minutes`    | `10`                                               | Minutes a task can stay `PROCESSING` before being considered a zombie |
| `task.recovery.retry-delay-seconds`  | `10`                                               | Delay before retrying a recovered zombie task                         |
| `task.retry.max-retries`             | `3`                                                | Max retry attempts before marking `FAILED`                            |
| `task.retry.initial-backoff-seconds` | `30`                                               | Initial backoff, doubled on each subsequent failure                   |

## Project Structure

```
src/main/java/by/art/taskprocessingsystem
├── config/           Spring configuration (JPA auditing, async executor, Swagger, clock)
├── controller/        REST controllers
├── dto/               Request/response records
├── entity/            JPA entities and enums
├── exception/         Custom exceptions and global exception handling
├── mapper/            MapStruct mappers
├── repository/        Spring Data JPA repositories
└── service/           Business logic (CRUD, lifecycle, execution, scheduling, retry, handlers)
```

## Adding a New Task Type

1. Add a new value to `TaskType`.
2. Implement `TaskHandler` for the new type and annotate it `@Component`.
3. The `TaskHandlerRegistry` will pick it up automatically at startup — no further wiring needed.