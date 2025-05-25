package dev.genro.luan.packing_test.domain.service;

import dev.genro.luan.packing_test.interfaces.dto.OrdersResponse;
import dev.genro.luan.packing_test.interfaces.dto.PackageOrderRequest;

public interface PackagingService {
  OrdersResponse packOrders(PackageOrderRequest orderRequests);
}