package dev.genro.luan.packing_test.configuration;

import dev.genro.luan.packing_test.domain.model.BoxType;
import dev.genro.luan.packing_test.domain.model.Dimension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

@Configuration
public class BoxConfiguration {

  @Bean
  public List<BoxType> availableBoxTypes() {
    List<BoxType> boxes = List.of(
        new BoxType("Box 1", new Dimension(30, 40, 80)),
        new BoxType("Box 2", new Dimension(80, 50, 40)),
        new BoxType("Box 3", new Dimension(50, 80, 60))
    );
    return boxes.stream()
        .sorted(Comparator.comparingLong(BoxType::volume))
        .toList();
  }
}
