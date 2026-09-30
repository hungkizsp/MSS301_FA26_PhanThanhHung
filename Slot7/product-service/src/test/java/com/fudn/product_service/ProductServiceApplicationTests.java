package com.fudn.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test cho ProductService sử dụng Testcontainers.
 *
 * Testcontainers tự động khởi động một MongoDB container thực sự (qua Docker)
 * trước khi test chạy và dừng nó sau khi test kết thúc.
 *
 * @SpringBootTest(RANDOM_PORT)  – tải toàn bộ Application Context, dùng cổng ngẫu nhiên
 * @Testcontainers               – kích hoạt quản lý vòng đời container
 * @AutoConfigureMockMvc         – tự cấu hình MockMvc để gọi HTTP mà không cần server thực
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    // Khai báo MongoDB container – static để dùng chung cho tất cả test methods
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    /**
     * Override spring.data.mongodb.uri bằng URI từ container Testcontainers.
     * Phải dùng @DynamicPropertySource vì URI chỉ biết được sau khi container khởi động.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    ObjectMapper objectMapper;

    /** Xóa toàn bộ dữ liệu trước mỗi test để đảm bảo test độc lập */
    @BeforeEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // Arrange – chuẩn bị dữ liệu request
        ProductRequest productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(BigDecimal.valueOf(19.99))
                .build();

        // Act & Assert – gọi POST /api/products và kiểm tra response
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("This is a test product"))
                .andExpect(jsonPath("$.price").value(19.99));

        // Assert – kiểm tra dữ liệu đã được lưu vào MongoDB
        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void contextLoads() {
        // Kiểm tra Application Context load thành công
    }
}
