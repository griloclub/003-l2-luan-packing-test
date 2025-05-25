package dev.genro.luan.packing_test.interfaces.dto;

import java.util.List;

public record InputValidationResponse(List<InputValidationItem> validations) {
  public record InputValidationItem(String field, List<String> messages) {}
}
