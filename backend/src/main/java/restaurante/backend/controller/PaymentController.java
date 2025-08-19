package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.PaymentIntentRequest;
import restaurante.backend.dto.PaymentIntentResponse;
import restaurante.backend.entity.Payment;
import restaurante.backend.service.PaymentService;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@Valid @RequestBody PaymentIntentRequest request) {
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(request);
            if (response.getMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new PaymentIntentResponse("Error creating payment intent: " + e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Payment> confirmPayment(@RequestParam String paymentIntentId) {
        try {
            Payment payment = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Webhook para recibir eventos de Stripe
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            paymentService.handleStripeWebhook(payload);
            return ResponseEntity.ok("Webhook handled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook handling failed: " + e.getMessage());
        }
    }
}
