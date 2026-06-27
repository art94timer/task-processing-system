package by.art.taskprocessingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;


public record ApiErrorResponse(
        @Schema(
                description = "Error title",
                example = "Task not found"
        )
        String title,
        @Schema(
                description = "Http status code",
                example = "400"
        )
        int status,

        @Schema(
                description = "Error detail",
                example = "Task with this id not found"
        )
        String detail,
        @Schema(
                description = "Validation errors by field"
        )
        Map<String, String> errors) {
}
