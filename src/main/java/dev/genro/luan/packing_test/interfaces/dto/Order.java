package dev.genro.luan.packing_test.interfaces.dto;

import java.util.List;

public record Order(int orderId, List<Product> products) {
}
