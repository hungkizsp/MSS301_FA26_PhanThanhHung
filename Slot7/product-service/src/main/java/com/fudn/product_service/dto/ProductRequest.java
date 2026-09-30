package com.fudn.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) – dữ liệu client gửi lên khi tạo sản phẩm.
 *
 * Sử dụng Lombok @Builder để test có thể dùng:
 *   ProductRequest.builder().name("...").description("...").price(...).build()
 *
 * Lý do không dùng Java Record ở đây: Record là immutable và không có @Builder
 * sẵn, trong khi test yêu cầu Builder pattern.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private String name;

    private String description;

    private BigDecimal price;
}
