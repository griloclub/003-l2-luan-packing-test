package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderRequest(@JsonProperty(value = "order_id") Integer orderId, List<ProductRequest> products) {
}
