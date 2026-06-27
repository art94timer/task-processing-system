package by.art.taskprocessingsystem.controller;

import by.art.taskprocessingsystem.dto.ApiErrorResponse;
import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.PageResponse;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${api.base.url}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task successfully created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )),
    })
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id", responses =
            {
                    @ApiResponse(responseCode = "200", description = "Task successfully received",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    public TaskResponse getTask(@PathVariable UUID id) {
        return taskService.getTask(id);
    }

    @GetMapping
    @Operation(summary = "Get tasks", responses =
            {
                    @ApiResponse(responseCode = "200", description = "Tasks successfully received",
                            content = @Content(schema = @Schema(
                                    implementation = PageResponse.class, subTypes = TaskResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            })
    public PageResponse<TaskResponse> getTasks(@ParameterObject @PageableDefault(
            size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update task", responses =
            {
                    @ApiResponse(responseCode = "200", description = "Tasks successfully updated",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            })
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse updateTask(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }
}
