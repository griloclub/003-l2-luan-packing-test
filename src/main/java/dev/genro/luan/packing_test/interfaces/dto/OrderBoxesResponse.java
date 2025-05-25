package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderBoxesResponse(@JsonProperty(value = "order_id") Integer orderId, List<PackedBoxResponse> boxes) {
}
