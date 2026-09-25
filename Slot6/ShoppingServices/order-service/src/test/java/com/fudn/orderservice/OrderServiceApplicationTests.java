package com.fudn.orderservice;

import com.fudn.orderservice.client.InventoryClient;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Integration Test cho Order Service.
 *
 * Thay vì WireMock (bị xóa trong Spring Cloud Contract 5.x),
 * dùng @MockBean để mock thẳng InventoryClient bean.
 *
 * Flow:
 *  1. Testcontainer khởi động MySQL – @ServiceConnection tự cấu hình datasource
 *  2. @MockBean tạo Mockito mock cho InventoryClient – thay thế bean thật trong Spring context
 *  3. when(inventoryClient.isInStock(...)).thenReturn(true) – stub kết quả
 *  4. RestAssured gửi POST /api/order → OrderService gọi mock → trả true → lưu DB → 201
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");

    @MockBean
    InventoryClient inventoryClient; // Mockito mock – thay thế Feign client thật

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldSubmitOrder() {
        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 1
                }
                """;

        // Stub: khi Feign gọi isInStock("iphone_15", 1) → trả true (còn hàng)
        when(inventoryClient.isInStock("iphone_15", 1)).thenReturn(true);

        var responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().asString();

        assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
    }

    @Test
    void shouldFailWhenOutOfStock() {
        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 101
                }
                """;

        // Stub: hết hàng → trả false
        when(inventoryClient.isInStock("iphone_15", 101)).thenReturn(false);

        RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(500); // RuntimeException → 500
    }
}