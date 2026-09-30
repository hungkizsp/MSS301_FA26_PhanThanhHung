package com.fudn.product_service.controller;

import com.fudn.product_service.dto.ProductRequest;
import com.fudn.product_service.dto.ProductResponse;
import com.fudn.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller – tiếp nhận HTTP request và trả về HTTP response.
 *
 * Không chứa business logic; chỉ delegate sang ProductService.
 *
 * Base URL: /api/products
 *
 * Endpoints:
 *   POST   /api/products  → tạo sản phẩm mới    (201 CREATED)
 *   GET    /api/products  → lấy danh sách       (200 OK)
 *
 * @RestController        – kết hợp @Controller + @ResponseBody (serialize kết quả thành JSON)
 * @RequestMapping        – đặt base path cho tất cả endpoint trong class
 * @RequiredArgsConstructor – Lombok tạo constructor injection
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * POST /api/products
     * Tạo sản phẩm mới từ dữ liệu client gửi lên.
     *
     * @param productRequest dữ liệu sản phẩm (JSON body)
     * @return ProductResponse với id được tạo, HTTP 201 CREATED
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    /**
     * GET /api/products
     * Lấy toàn bộ danh sách sản phẩm từ MongoDB.
     *
     * @return danh sách ProductResponse, HTTP 200 OK
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
