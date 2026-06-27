package by.art.taskprocessingsystem.exception;

import by.art.taskprocessingsystem.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String TASK_NOT_FOUND_TITLE = "Task not found";

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleTaskNotFound(
            TaskNotFoundException ex) {
        return new ApiErrorResponse(TASK_NOT_FOUND_TITLE, HttpStatus.NOT_FOUND.value(),
                "Task with this id not found",
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(
            MethodArgumentNotValidException ex) {
        return new ApiErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                ex.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                field -> Objects.toString(field.getDefaultMessage(), StringUtils.EMPTY),
                                (first, second) -> first)
                        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(
            MethodArgumentTypeMismatchException ex) {
        return new ApiErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                Map.of(Objects.toString(ex.getPropertyName(), StringUtils.EMPTY),
                        "Incorrect value"));
    }
}
