package by.art.taskprocessingsystem.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String TASK_NOT_FOUND_TITLE = "Task not found";

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleTaskNotFound(
            TaskNotFoundException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(TASK_NOT_FOUND_TITLE);
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
