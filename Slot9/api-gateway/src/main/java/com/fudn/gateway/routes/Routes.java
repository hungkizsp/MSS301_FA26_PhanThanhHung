package com.fudn.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

/**
 * Dinh nghia routing rules cua API Gateway (Spring Cloud Gateway Server Web MVC).
 * Moi route = ID + Predicate (dieu kien) + Handler http() + Before-filter uri(url).
 *
 * API tu Spring Cloud 2025.1: http(String) da bi xoa,
 * thay bang http() + .before(uri("http://..."))
 */
@Configuration(proxyBeanMethods = false)
public class Routes {

    @Value("${services.product.url:http://localhost:8080}")
    private String productServiceUrl;

    @Value("${services.order.url:http://localhost:8081}")
    private String orderServiceUrl;

    @Value("${services.inventory.url:http://localhost:8082}")
    private String inventoryServiceUrl;

    // TODO 3.13: Route product-service
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return route("product_service")
                .route(RequestPredicates.path("/api/products/**"), http())
                .before(uri(productServiceUrl))
                .build();
    }

    // TODO 3.13: Route order-service
    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route("order_service")
                .route(RequestPredicates.path("/api/order/**"), http())
                .before(uri(orderServiceUrl))
                .build();
    }

    // TODO 3.13: Route inventory-service
    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory/**"), http())
                .before(uri(inventoryServiceUrl))
                .build();
    }

    // TODO DOC-15a: Swagger route - Product Service
    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri(productServiceUrl))
                .build();
    }

    // TODO DOC-15b: Swagger route - Order Service
    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri(orderServiceUrl))
                .build();
    }

    // TODO DOC-15c: Swagger route - Inventory Service
    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri(inventoryServiceUrl))
                .build();
    }
}
