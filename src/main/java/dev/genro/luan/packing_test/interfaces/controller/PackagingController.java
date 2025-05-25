package dev.genro.luan.packing_test.interfaces.controller;

import dev.genro.luan.packing_test.domain.service.PackagingService;
import dev.genro.luan.packing_test.interfaces.dto.OrderBoxesResponse;
import dev.genro.luan.packing_test.interfaces.dto.OrdersResponse;
import dev.genro.luan.packing_test.interfaces.dto.PackageOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/packaging")
public class PackagingController {

  private final PackagingService packagingService;

  public PackagingController(PackagingService packagingService) {
    this.packagingService = packagingService;
  }

  @PostMapping("/optimize")
  @Operation(summary = "Optimize product packaging for orders",
      description = "Receives a list of orders with products and their dimensions, " +
          "and returns the suggested boxing solution to minimize the number of boxes used.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully calculated packaging",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OrderBoxesResponse.class)))
      })
  public ResponseEntity<OrdersResponse> optimizePackaging(
      @Valid @RequestBody PackageOrderRequest packageOrderRequest) {
    OrdersResponse responses = packagingService.packOrders(packageOrderRequest);
    return ResponseEntity.ok(responses);
  }
}
