package dev.genro.luan.packing_test.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiErrorResponse(
    @Schema(description = "Timestamp of the error occurrence in milliseconds since epoch", example = "1678886400000")
    Long timestamp,

    @Schema(description = "HTTP status code", example = "401")
    int status,

    @Schema(description = "Error category", example = "Unauthorized")
    String error,

    @Schema(description = "Detailed error message", example = "Authentication token was missing, invalid or expired")
    String message,

    @Schema(description = "The path for which the error occurred", example = "/api/v1/packaging/optimize")
    String path
) {
}
