package dev.genro.luan.packing_test.domain.model;

public record Product(String productId, Dimension dimension) {
  public long volume() {
    return dimension.volume();
  }
}
