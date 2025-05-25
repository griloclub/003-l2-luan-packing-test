package dev.genro.luan.packing_test.exception;

import dev.genro.luan.packing_test.interfaces.dto.InputValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<InputValidationResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    Map<String, List<String>> errorsByField =
        e.getBindingResult().getAllErrors().stream()
            .filter(error -> error instanceof FieldError)
            .map(error -> (FieldError) error)
            .collect(
                Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

    List<InputValidationResponse.InputValidationItem> validations =
        errorsByField.entrySet().stream()
            .map(
                entry ->
                    new InputValidationResponse.InputValidationItem(
                        entry.getKey(), entry.getValue()))
            .toList();

    return ResponseEntity.badRequest().body(new InputValidationResponse(validations));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<InputValidationResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    List<InputValidationResponse.InputValidationItem> validationItems = new ArrayList<>();
    validationItems.add(
        new InputValidationResponse.InputValidationItem(
            null,
            List.of("Request body informed is invalid, not able to parse as JSON.")));
    return ResponseEntity.badRequest().body(new InputValidationResponse(validationItems));
  }
}
