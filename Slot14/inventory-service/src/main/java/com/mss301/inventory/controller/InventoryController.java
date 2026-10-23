package com.mss301.inventory.controller;

import com.mss301.inventory.dto.ReserveRequest;
import com.mss301.inventory.entity.*;
import com.mss301.inventory.repository.*;
import com.mss301.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    @PostMapping("/reserve")
    public Reservation reserve(@RequestBody ReserveRequest req) {
        return inventoryService.reserve(req);
    }

    @PostMapping("/release/{orderId}")
    public String release(@PathVariable Long orderId) {
        inventoryService.release(orderId);
        return "RELEASED";
    }

    @GetMapping("/products")
    public List<Product> products() {
        return productRepository.findAll();
    }

    @GetMapping("/reservations")
    public List<Reservation> reservations() {
        return reservationRepository.findAll();
    }

    /** Reset dữ liệu test: xoá tất cả reservation, khôi phục stock P1=10, P2=5 */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> reset() {
        reservationRepository.deleteAll();
        productRepository.deleteAll();
        productRepository.save(new Product("P1", "Laptop", 1000, 10));
        productRepository.save(new Product("P2", "Mouse", 20, 5));
        return ResponseEntity.ok(Map.of("status", "RESET_OK", "P1", "10", "P2", "5"));
    }
}
