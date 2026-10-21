package com.mss301.payment.controller;

import com.mss301.payment.dto.PaymentRequest;
import com.mss301.payment.entity.*;
import com.mss301.payment.repository.*;
import com.mss301.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping("/pay")
    public Payment pay(@RequestBody PaymentRequest req) {
        return paymentService.pay(req);
    }

    @PostMapping("/refund/{orderId}")
    public String refund(@PathVariable Long orderId) {
        paymentService.refund(orderId);
        return "REFUNDED";
    }

    @GetMapping("/wallets")
    public List<Wallet> wallets() {
        return walletRepository.findAll();
    }

    @GetMapping
    public List<Payment> payments() {
        return paymentRepository.findAll();
    }

    /** Reset dữ liệu test: xoá tất cả payment, khôi phục ví về trạng thái ban đầu */
    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<Map<String, String>> reset() {
        paymentRepository.deleteAll();
        walletRepository.deleteAll();
        walletRepository.save(new Wallet("C1", 5000));
        walletRepository.save(new Wallet("C2", 100));
        return ResponseEntity.ok(Map.of("status", "RESET_OK", "C1", "5000", "C2", "100"));
    }
}
