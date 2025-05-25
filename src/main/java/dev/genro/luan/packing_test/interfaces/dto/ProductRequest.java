package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductRequest(@JsonProperty(value = "product_id") String productId, DimensionRequest dimension) {
}
