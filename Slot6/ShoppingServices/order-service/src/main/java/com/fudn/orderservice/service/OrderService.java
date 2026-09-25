package com.fudn.orderservice.service;

import com.fudn.orderservice.client.InventoryClient;
import com.fudn.orderservice.dto.OrderRequest;
import com.fudn.orderservice.model.Order;
import com.fudn.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient; // Feign client – giao tiếp với Inventory Service

    public void placeOrder(OrderRequest orderRequest) {
        // Bước 1: Kiểm tra tồn kho qua Feign (gọi HTTP đồng bộ tới Inventory Service)
        boolean inStock = inventoryClient.isInStock(
                orderRequest.skuCode(),
                orderRequest.quantity());

        // Bước 2: Chỉ lưu đơn nếu còn hàng
        if (inStock) {
            var order = mapToOrder(orderRequest);
            orderRepository.save(order);
        } else {
            // Nếu hết hàng → throw exception → @Transactional rollback → không lưu vào DB
            throw new RuntimeException(
                    "Product with Skucode " + orderRequest.skuCode() + " is not in stock");
        }
    }

    private static Order mapToOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());
        return order;
    }
}