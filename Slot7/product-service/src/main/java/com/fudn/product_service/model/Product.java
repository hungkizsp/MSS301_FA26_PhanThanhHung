package com.fudn.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Domain Model – maps to the "product" collection in MongoDB.
 *
 * Annotations:
 *   @Document  – tells Spring Data MongoDB which collection to use
 *   @Id        – marks the primary key field (MongoDB ObjectId)
 *   @Data      – Lombok: generates getters, setters, equals, hashCode, toString
 *   @Builder   – Lombok: enables the Builder pattern
 *   @AllArgsConstructor / @NoArgsConstructor – required by Builder + MongoDB deserialization
 */
@Document(value = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal price;
}
