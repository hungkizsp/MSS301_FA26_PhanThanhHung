package com.fudn.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Declarative HTTP client gọi sang Inventory Service.
 * Spring Cloud OpenFeign tự sinh implementation lúc runtime qua dynamic proxy.
 *
 * isInStock("iphone_15", 1)
 *   ==> GET ${inventory.url}/api/inventory?skuCode=iphone_15&quantity=1
 */
@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam("skuCode") String skuCode,
                      @RequestParam("quantity") Integer quantity);
}
