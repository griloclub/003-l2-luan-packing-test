package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(@JsonProperty(value = "product_id")
                             @NotNull(message = "Product identifier must be informed") String productId,
                             @Valid @NotNull(message = "Product dimensions bust me informed") DimensionRequest dimension) {
}
