package by.art.taskprocessingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Page response")
public record PageResponse<T>(
        @Schema(description = "Content")
        List<T> content,

        @Schema(
                description = "Total number of elements",
                example = "150"
        )
        long totalElements,

        @Schema(
                description = "Total number of pages",
                example = "8"
        )
        int totalPages,

        @Schema(
                description = "Page size",
                example = "20"
        )
        int size,

        @Schema(
                description = "Number of elements currently on this page",
                example = "20"
        )
        int numberOfElements,

        @Schema(
                description = "Current page number",
                example = "0"
        )
        int page,

        @Schema(
                description = "Whether this is the first page",
                example = "false"
        )
        boolean first,

        @Schema(
                description = "Whether this is the last page",
                example = "false"
        )
        boolean last) {
}
