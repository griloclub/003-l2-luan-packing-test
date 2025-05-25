package dev.genro.luan.packing_test.domain.model;

public record Dimension(int height, int width, int length) {

  public long volume() {
    return (long) height * width * length;
  }

  /**
   * Checks if this dimension can fit into the targetDimension, considering all 6 rotations.
   * Assumes the product can be rotated freely.
   *
   * @param targetDimension The dimensions of the box to fit into.
   * @return true if any orientation of this product fits into the box, false otherwise.
   */
  public boolean canFitInto(Dimension targetDimension) {
    int pL = this.length;
    int pW = this.width;
    int pH = this.height;

    int bL = targetDimension.length();
    int bW = targetDimension.width();
    int bH = targetDimension.height();

    return checkOrientation(pL, pW, pH, bL, bW, bH) ||
        checkOrientation(pL, pH, pW, bL, bW, bH) ||
        checkOrientation(pW, pL, pH, bL, bW, bH) ||
        checkOrientation(pW, pH, pL, bL, bW, bH) ||
        checkOrientation(pH, pL, pW, bL, bW, bH) ||
        checkOrientation(pH, pW, pL, bL, bW, bH);
  }

  private boolean checkOrientation(int currentProductLength, int currentProductWidth, int currentProductHeight,
                                   int boxLength, int boxWidth, int boxHeight) {
    return (currentProductLength <= boxLength && currentProductWidth <= boxWidth && currentProductHeight <= boxHeight);
  }
}
