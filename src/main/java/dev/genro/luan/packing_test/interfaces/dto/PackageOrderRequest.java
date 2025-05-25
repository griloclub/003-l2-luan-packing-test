package dev.genro.luan.packing_test.interfaces.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PackageOrderRequest(@Valid @NotEmpty(message = "Orders must be informed") List<OrderRequest> orders) {
}
