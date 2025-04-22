package com.bookinline.bookinline.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Error object for API responses")
public class ErrorObject {
    @Schema(description = "HTTP status code", example = "400")
    private Integer statusCode;
    @Schema(description = "Error message", example = "Invalid property data")
    private String message;
    @Schema(description = "Error timestamp", example = "2025-04-23T12:00:00Z")
    private Date timestamp;
}
