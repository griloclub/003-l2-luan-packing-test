package dev.genro.luan.packing_test.interfaces.controller;

import dev.genro.luan.packing_test.domain.service.PackagingService;
import dev.genro.luan.packing_test.interfaces.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/packaging")
@Tag(name = "Packaging Optimization", description = "API for optimizing product packaging into boxes.")
public class PackagingController {

  private final PackagingService packagingService;

  public PackagingController(PackagingService packagingService) {
    this.packagingService = packagingService;
  }

  @PostMapping("/optimize")
  @Operation(summary = "Optimize product packaging for orders",
      description = "Receives a list of orders with products and their dimensions, " +
          "and returns the suggested boxing solution to minimize the number of boxes used.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "List of orders to be processed for packaging optimization.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PackageOrderRequest.class),
              examples = @ExampleObject(
                  name = "Sample Packaging Request",
                  summary = "Example payload with multiple orders and products",
                  value = """
                        {
                        "orders": [
                          {
                            "order_id": 1,
                            "products": [
                              {
                                "product_id": "PS5",
                                "dimension": {
                                  "height": 40,
                                  "width": 10,
                                  "length": 25
                                }
                              },
                              {
                                "product_id": "Volante",
                                "dimension": {
                                  "height": 40,
                                  "width": 30,
                                  "length": 30
                                }
                              }
                            ]
                          },
                          {
                            "order_id": 2,
                            "products": [
                              {
                                "product_id": "Joystick",
                                "dimension": {
                                  "height": 15,
                                  "width": 20,
                                  "length": 10
                                }
                              },
                              {
                                "product_id": "Fifa 24",
                                "dimension": {
                                  "height": 10,
                                  "width": 30,
                                  "length": 10
                                }
                              },
                              {
                                "product_id": "Call of Duty",
                                "dimension": {
                                  "height": 30,
                                  "width": 15,
                                  "length": 10
                                }
                              }
                            ]
                          },
                          {
                            "order_id": 5,
                            "products": [
                              {
                                "product_id": "Cadeira Gamer",
                                "dimension": {
                                  "height": 120,
                                  "width": 60,
                                  "length": 70
                                }
                              }
                            ]
                          }
                        ]
                      }
                      """
              )
          )
      ),
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully calculated packaging",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OrderBoxesResponse.class))),
          @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing, invalid, or expired",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ApiErrorResponse.class))),
          @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", // Example if you also handle validation errors
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = InputValidationResponse.class))) // You might have a different DTO for validation errors
      })
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<OrdersResponse> optimizePackaging(
      @Valid @RequestBody PackageOrderRequest packageOrderRequest) {
    OrdersResponse responses = packagingService.packOrders(packageOrderRequest);
    return ResponseEntity.ok(responses);
  }
}
