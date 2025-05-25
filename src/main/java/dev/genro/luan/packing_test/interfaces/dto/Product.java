package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Product(@JsonProperty(value = "product_id") String productId, Dimensions dimensions) {
}
