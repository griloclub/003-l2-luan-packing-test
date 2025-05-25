package dev.genro.luan.packing_test.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record InputValidationResponse(@Schema(description = "A list of triggered validations")
                                      List<InputValidationItem> validations) {
  public record InputValidationItem(@Schema(description = "Validated field") String field,
                                    @Schema(description = "A list of validation messages") List<String> messages) {}
}
