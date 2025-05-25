package dev.genro.luan.packing_test.domain.model;

public record BoxType(String name, Dimension dimension) {
  public long volume() {
    return dimension.volume();
  }
}
