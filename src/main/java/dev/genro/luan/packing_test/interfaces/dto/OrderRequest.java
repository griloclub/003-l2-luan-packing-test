package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(@JsonProperty(value = "order_id")
                           @Positive(message = "Order identifier must be a positive number") Integer orderId,
                           @NotEmpty(message = "Products must be informed") @Valid List<ProductRequest> products) {
}
