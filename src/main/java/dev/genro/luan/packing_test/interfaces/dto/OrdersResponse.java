package dev.genro.luan.packing_test.interfaces.dto;

import java.util.List;

public record OrdersResponse(List<OrderBoxesResponse> orders) {
}
