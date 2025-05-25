package dev.genro.luan.packing_test;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "L2 Code - Packaging Test API", version = "1.0",
    description = "API for Seu Manoel's store to automate package selection for orders."))
public class PackingTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(PackingTestApplication.class, args);
  }

}
