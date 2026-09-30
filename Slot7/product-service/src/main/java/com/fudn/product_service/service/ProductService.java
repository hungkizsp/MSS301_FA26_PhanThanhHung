package com.fudn.product_service.service;

import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.dto.ProductResponse;
import com.fudn.product_service.model.Product;
import com.fudn.product_service.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer – chứa toàn bộ business logic cho Product.
 *
 * Nhiệm vụ chính:
 *   1. Nhận DTO từ Controller, ánh xạ sang Domain Model để lưu DB.
 *   2. Lấy dữ liệu từ DB, ánh xạ sang DTO để trả về Controller.
 *   3. Ghi log trạng thái xử lý với SLF4J.
 *
 * @Service  – đăng ký bean với Spring IoC container
 * @RequiredArgsConstructor – Lombok tự tạo constructor injection cho IProductRepository
 * @Slf4j    – Lombok tự tạo biến log (SLF4J)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final IProductRepository productRepository;

    /**
     * Tạo sản phẩm mới và lưu vào MongoDB.
     *
     * @param productRequest DTO chứa dữ liệu từ client
     * @return ProductResponse với id được MongoDB tự sinh
     */
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Ánh xạ từ Request DTO → Domain Model (dùng Builder Pattern)
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        // Lưu vào MongoDB – Spring Data tự sinh ObjectId
        product = productRepository.save(product);

        log.info("Product saved successfully with id: {}", product.getId());

        // Ánh xạ từ Domain Model → Response DTO và trả về
        return mapToProductResponse(product);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm từ MongoDB.
     *
     * @return danh sách ProductResponse
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();  // Java 16+ – immutable list
    }

    // ── Private helper ──────────────────────────────────────────────────────
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
