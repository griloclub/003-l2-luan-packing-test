package dev.genro.luan.packing_test.interfaces.dto;

import jakarta.validation.constraints.Positive;

public record DimensionRequest(@Positive(message = "Product height must be a positive number") int height,
                               @Positive(message = "Product width must be a positive number") int width,
                               @Positive(message = "Product length must be a positive number") int length) {
}
