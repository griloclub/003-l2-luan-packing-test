package dev.genro.luan.packing_test.interfaces.dto;

import java.util.List;

public record PackageOrderRequest(List<OrderRequest> orders) {
}
