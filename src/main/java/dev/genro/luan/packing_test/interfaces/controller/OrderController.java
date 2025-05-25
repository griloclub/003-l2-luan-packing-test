package dev.genro.luan.packing_test.interfaces.controller;

import dev.genro.luan.packing_test.interfaces.dto.PackageOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orders")
@RestController
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @PostMapping("/package")
  public ResponseEntity<HttpStatus> packageOrders(@RequestBody PackageOrderRequest request) {
    log.info("Received request: {}", request.toString());
    return ResponseEntity.ok().build();
  }

}
