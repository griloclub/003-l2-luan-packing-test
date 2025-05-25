package dev.genro.luan.packing_test.domain.service;

import dev.genro.luan.packing_test.domain.model.BoxType;
import dev.genro.luan.packing_test.domain.model.Dimension;
import dev.genro.luan.packing_test.interfaces.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PackagingServiceImplTest {

  private PackagingServiceImpl packagingService;
  private List<BoxType> testBoxTypes;

  @BeforeEach
  void setUp() {
    testBoxTypes = Stream.of(
        new BoxType("Box 1", new Dimension(30, 40, 80)),
        new BoxType("Box 2", new Dimension(80, 50, 40)),
        new BoxType("Box 3", new Dimension(50, 80, 60))
    ).sorted(Comparator.comparingLong(BoxType::volume)).toList();

    packagingService = new PackagingServiceImpl(testBoxTypes);
  }

  private ProductRequest createProductRequest(String id, int h, int w, int l) {
    return new ProductRequest(id, new DimensionRequest(h, w, l));
  }

  @Test
  @DisplayName("Should pack a single small product into the smallest available box")
  void packOrders_whenSingleProductFitsSmallestBox_shouldPackCorrectly() {
    ProductRequest product1 = createProductRequest("product 1", 10, 10, 10);
    OrderRequest orderRequest = new OrderRequest(1, List.of(product1));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    assertNotNull(ordersResponse);
    assertEquals(1, ordersResponse.orders().size());

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(1, orderBoxesResponse.orderId());
    assertEquals(1, orderBoxesResponse.boxes().size());

    PackedBoxResponse packedBox = orderBoxesResponse.boxes().getFirst();
    assertEquals(testBoxTypes.getFirst().name(), packedBox.boxId());
    assertEquals(1, packedBox.products().size());
    assertTrue(packedBox.products().contains("product 1"));
    assertNull(packedBox.observation());
  }

  @Test
  @DisplayName("Should pack multiple products into the smallest suitable box that fits the largest product first")
  void packOrders_whenMultipleProductsFitSingleBox_shouldUseSmallestSufficientBox() {
    ProductRequest product1 = createProductRequest("p1", 40, 70, 50);
    ProductRequest product2 = createProductRequest("p2", 5, 5, 5);

    OrderRequest orderRequest = new OrderRequest(2, List.of(product1, product2));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    assertNotNull(ordersResponse.orders());
    assertEquals(1, ordersResponse.orders().size());
    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(1, orderBoxesResponse.boxes().size());

    PackedBoxResponse packedBox = orderBoxesResponse.boxes().getFirst();
    assertEquals(testBoxTypes.get(1).name(), packedBox.boxId());
    assertEquals(2, packedBox.products().size());
    assertTrue(packedBox.products().containsAll(List.of("p1", "p2")));
  }

  @Test
  @DisplayName("Should mark a single product as unpackable if it's too large for any box")
  void packOrders_whenSingleProductIsUnpackable_shouldReturnSpecialEntry() {
    ProductRequest hugeProduct = createProductRequest("hugeP", 100, 100, 100);
    OrderRequest orderRequest = new OrderRequest(4, List.of(hugeProduct));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(1, orderBoxesResponse.boxes().size());

    PackedBoxResponse specialEntry = orderBoxesResponse.boxes().getFirst();
    assertNull(specialEntry.boxId());
    assertEquals(1, specialEntry.products().size());
    assertTrue(specialEntry.products().contains("hugeP"));
    assertEquals("Product does not fit in any available box.", specialEntry.observation());
  }

  @Test
  @DisplayName("Should handle mixed packable and unpackable products in an order")
  void packOrders_whenMixedPackableAndUnpackableProducts_shouldHandleBoth() {
    ProductRequest packableProduct = createProductRequest("packP", 10, 10, 10);
    ProductRequest unpackableProduct = createProductRequest("unpackP", 200, 200, 200);
    OrderRequest orderRequest = new OrderRequest(5, List.of(packableProduct, unpackableProduct));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(2, orderBoxesResponse.boxes().size());

    Optional<PackedBoxResponse> packedBoxOpt = orderBoxesResponse.boxes().stream()
        .filter(b -> b.products().contains("packP") && b.boxId() != null)
        .findFirst();
    assertTrue(packedBoxOpt.isPresent());
    assertEquals(testBoxTypes.getFirst().name(), packedBoxOpt.get().boxId());
    assertNull(packedBoxOpt.get().observation());

    Optional<PackedBoxResponse> unpackableBoxOpt = orderBoxesResponse.boxes().stream()
        .filter(b -> b.products().contains("unpackP") && b.boxId() == null)
        .findFirst();
    assertTrue(unpackableBoxOpt.isPresent());
    assertEquals("Product does not fit in any available box.", unpackableBoxOpt.get().observation());
  }

  @Test
  @DisplayName("Should group all unpackable products in an order into a single special entry")
  void packOrders_whenAllProductsInOrderAreUnpackable_shouldGroupInSpecialEntry() {
    ProductRequest unpackable1 = createProductRequest("unpackP1", 100, 100, 100);
    ProductRequest unpackable2 = createProductRequest("unpackP2", 150, 150, 150);
    OrderRequest orderRequest = new OrderRequest(6, List.of(unpackable1, unpackable2));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(1, orderBoxesResponse.boxes().size());

    PackedBoxResponse specialEntry = orderBoxesResponse.boxes().getFirst();
    assertNull(specialEntry.boxId());
    assertEquals(2, specialEntry.products().size());
    assertTrue(specialEntry.products().containsAll(List.of("unpackP1", "unpackP2")));
    assertEquals("These products do not fit into any available boxes.", specialEntry.observation());
  }

  @Test
  @DisplayName("Should return an empty boxes list for an order with no products")
  void packOrders_whenOrderHasNoProducts_shouldReturnEmptyBoxesList() {
    OrderRequest orderRequest = new OrderRequest(7, Collections.emptyList());
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);
    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(7, orderBoxesResponse.orderId());
    assertTrue(orderBoxesResponse.boxes().isEmpty());
  }

  @Test
  @DisplayName("Should return an empty response list when input is an empty list of orders")
  void packOrders_whenInputIsEmptyOrderList_shouldReturnEmptyResponseList() {
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(Collections.emptyList());

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    assertNotNull(ordersResponse);
    assertTrue(ordersResponse.orders().isEmpty());
  }

  @Test
  @DisplayName("Should pack a product that requires rotation to fit")
  void packOrders_whenProductRequiresRotation_shouldFit() {
    ProductRequest productRequiresRotation = createProductRequest("pRotate", 40, 30, 79);
    OrderRequest orderRequest = new OrderRequest(8, List.of(productRequiresRotation));
    PackageOrderRequest packageOrderRequest = new PackageOrderRequest(List.of(orderRequest));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequest);

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(1, orderBoxesResponse.boxes().size());
    PackedBoxResponse packedBox = orderBoxesResponse.boxes().getFirst();
    assertEquals(testBoxTypes.getFirst().name(), packedBox.boxId());
    assertTrue(packedBox.products().contains("pRotate"));
  }

  @Test
  @DisplayName("Should pack products into smallest possible new boxes after trying existing ones")
  void packOrders_shouldUseSmallestNewBoxes() {
    ProductRequest itemForBox3 = createProductRequest("itemC3", 50, 79, 59);
    ProductRequest itemForBox2 = createProductRequest("itemC2", 79, 49, 39);
    ProductRequest itemForBox1 = createProductRequest("itemC1", 29, 39, 79);

    OrderRequest orderRequestNewBoxes = new OrderRequest(8, List.of(itemForBox1, itemForBox2, itemForBox3));
    PackageOrderRequest packageOrderRequestNewBoxes = new PackageOrderRequest(List.of(orderRequestNewBoxes));

    OrdersResponse ordersResponse = packagingService.packOrders(packageOrderRequestNewBoxes);

    OrderBoxesResponse orderBoxesResponse = ordersResponse.orders().getFirst();
    assertEquals(3, orderBoxesResponse.boxes().size());

    assertTrue(orderBoxesResponse.boxes().stream().anyMatch(b -> b.boxId().equals(testBoxTypes.get(2).name()) && b.products().contains("itemC3")));
    assertTrue(orderBoxesResponse.boxes().stream().anyMatch(b -> b.boxId().equals(testBoxTypes.get(1).name()) && b.products().contains("itemC2")));
    assertTrue(orderBoxesResponse.boxes().stream().anyMatch(b -> b.boxId().equals(testBoxTypes.getFirst().name()) && b.products().contains("itemC1")));
  }
}
