package com.fudn.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Declarative HTTP client gọi sang Inventory Service.
 * Spring OpenFeign tự sinh implementation lúc runtime qua dynamic proxy.
 *
 * url được externalize vào application.properties (key: inventory.url)
 * để dễ override theo môi trường (dev / staging / prod / test).
 */
@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryClient {

    /**
     * Kiểm tra tồn kho: trả true nếu skuCode còn đủ số lượng quantity.
     * Endpoint Inventory Service: GET /api/inventory?skuCode=...&quantity=...
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);
}
