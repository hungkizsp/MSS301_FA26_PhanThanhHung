package com.fudn.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO – dữ liệu server trả về cho client sau khi tạo / lấy sản phẩm.
 *
 * Bao gồm id (được MongoDB tự sinh), cùng các trường từ ProductRequest.
 * Việc tách riêng Request/Response DTO giúp:
 *   - Ẩn các trường nội bộ của Model (nếu có)
 *   - Linh hoạt thay đổi API contract mà không ảnh hưởng đến Model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private String id;

    private String name;

    private String description;

    private BigDecimal price;
}
