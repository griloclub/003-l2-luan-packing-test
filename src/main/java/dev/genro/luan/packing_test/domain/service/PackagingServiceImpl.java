package dev.genro.luan.packing_test.domain.service;

import dev.genro.luan.packing_test.domain.model.BoxType;
import dev.genro.luan.packing_test.domain.model.Dimension;
import dev.genro.luan.packing_test.domain.model.Product;
import dev.genro.luan.packing_test.interfaces.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackagingServiceImpl implements PackagingService {

  private static final Logger log = LoggerFactory.getLogger(PackagingServiceImpl.class);
  private final List<BoxType> availableBoxTypes;

  private record OpenBox(BoxType boxType, List<Product> packedProducts, long remainingVolume) {
    public OpenBox addProduct(Product product) {
      List<Product> newProductList = new ArrayList<>(packedProducts);
      newProductList.add(product);
      return new OpenBox(boxType, newProductList, remainingVolume - product.volume());
    }
  }

  public PackagingServiceImpl(List<BoxType> availableBoxTypes) {
    this.availableBoxTypes = availableBoxTypes;
    log.info("Initialized PackagingService with {} box types.", availableBoxTypes.size());
    availableBoxTypes.forEach(box -> log.info("Available Box: {} - Volume: {}", box.name(), box.volume()));
  }

  @Override
  public OrdersResponse packOrders(PackageOrderRequest orderRequests) {
    return new OrdersResponse(orderRequests.orders().stream()
        .map(this::packSingleOrder)
        .toList());
  }

  private OrderBoxesResponse packSingleOrder(OrderRequest orderRequest) {
    log.info("Processing order: {}", orderRequest.orderId());

    List<Product> productsToPack = orderRequest.products().stream()
        .map(pReq -> new Product(
            pReq.productId(),
            new Dimension(pReq.dimension().height(), pReq.dimension().width(), pReq.dimension().length())))
        .sorted(Comparator.comparingLong(Product::volume).reversed())
        .toList();

    List<OpenBox> currentlyOpenBoxes = new ArrayList<>();
    List<Product> unpackableProducts = new ArrayList<>();

    productsToPack.forEach(product -> {
      log.info("Attempting to pack product: {} with volume {} for order {}", product.productId(), product.volume(), orderRequest.orderId());
      boolean productPlaced = false;

      for (int i = 0; i < currentlyOpenBoxes.size(); i++) {
        OpenBox openBox = currentlyOpenBoxes.get(i);
        if (product.volume() <= openBox.remainingVolume() &&
            product.dimension().canFitInto(openBox.boxType().dimension())) {
          currentlyOpenBoxes.set(i, openBox.addProduct(product));
          productPlaced = true;
          log.info("Product {} for order {} placed in existing box: {}", product.productId(), orderRequest.orderId(), openBox.boxType().name());
          break;
        }
      }

      if (!productPlaced) {
        BoxType chosenNewBoxType = null;
        for (BoxType candidateBoxType : availableBoxTypes) {
          if (product.volume() <= candidateBoxType.volume() &&
              product.dimension().canFitInto(candidateBoxType.dimension())) {
            chosenNewBoxType = candidateBoxType;
            break;
          }
        }

        if (chosenNewBoxType != null) {
          OpenBox newBox = new OpenBox(chosenNewBoxType, new ArrayList<>(), chosenNewBoxType.volume());
          currentlyOpenBoxes.add(newBox.addProduct(product));
          log.info("Product {} for order {} placed in new box: {}", product.productId(), orderRequest.orderId(), chosenNewBoxType.name());
        } else {
          log.warn("Product {} (Volume: {}) for order {} could not be placed in any box.", product.productId(), product.volume(), orderRequest.orderId());
          unpackableProducts.add(product);
        }
      }
    });

    List<PackedBoxResponse> packedBoxesShipment = currentlyOpenBoxes.stream()
        .map(openBox -> new PackedBoxResponse(
            openBox.boxType().name(),
            openBox.packedProducts().stream()
                .map(Product::productId)
                .collect(Collectors.toList()),
            null
        ))
        .toList();

    List<PackedBoxResponse> finalBoxes = new ArrayList<>(packedBoxesShipment);

    if (!unpackableProducts.isEmpty()) {
      String observationMessage = unpackableProducts.size() == 1 ?
          "Product does not fit in any available box." :
          "These products do not fit into any available boxes.";

      finalBoxes.add(new PackedBoxResponse(
          null,
          unpackableProducts.stream().map(Product::productId).collect(Collectors.toList()),
          observationMessage
      ));
      log.info("{} unpackable product(s) for order {} added to special entry.", unpackableProducts.size(), orderRequest.orderId());
    }

    log.info("Finished processing order: {}. Used {} boxes.", orderRequest.orderId(), finalBoxes.size());
    return new OrderBoxesResponse(orderRequest.orderId(), finalBoxes);
  }
}
