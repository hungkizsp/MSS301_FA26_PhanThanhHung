package com.fudn.orderservice.stub;

import com.fudn.orderservice.client.InventoryClient;
import lombok.experimental.UtilityClass;

import static org.mockito.Mockito.when;

/**
 * Utility class chứa các stub Mockito cho InventoryClient.
 *
 * Lý do dùng Mockito thay WireMock:
 *  - @AutoConfigureWireMock bị xóa khỏi Spring Cloud Contract 5.x (2025.x)
 *  - @MockBean + Mockito đã có sẵn trong spring-boot-starter-test, không cần thêm dependency
 *  - Đủ để verify logic đặt hàng mà không cần HTTP thật
 */
@UtilityClass
public class InventoryStubs {

    /**
     * Stub: InventoryClient.isInStock trả true (còn hàng)
     */
    public void stubInventoryCallInStock(InventoryClient inventoryClient,
                                         String skuCode, Integer quantity) {
        when(inventoryClient.isInStock(skuCode, quantity)).thenReturn(true);
    }

    /**
     * Stub: InventoryClient.isInStock trả false (hết hàng)
     */
    public void stubInventoryCallOutOfStock(InventoryClient inventoryClient,
                                             String skuCode, Integer quantity) {
        when(inventoryClient.isInStock(skuCode, quantity)).thenReturn(false);
    }
}
