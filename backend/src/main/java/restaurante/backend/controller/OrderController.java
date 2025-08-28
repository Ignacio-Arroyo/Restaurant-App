package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.OrderRequest;
import restaurante.backend.entity.Order;
import restaurante.backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO') or hasRole('MESERO') or hasRole('CAJERO') or hasRole('AFANADOR')")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            System.out.println("=== DEBUG: Order request received ===");
            System.out.println("Meals: " + (orderRequest.getMeals() != null ? orderRequest.getMeals().size() : "null"));
            System.out.println("Drinks: " + (orderRequest.getDrinks() != null ? orderRequest.getDrinks().size() : "null"));
            System.out.println("Promotions: " + (orderRequest.getPromotions() != null ? orderRequest.getPromotions().size() : "null"));
            System.out.println("Total Cost: " + orderRequest.getTotalCost());
            System.out.println("Order Type: " + orderRequest.getOrderType());
            System.out.println("Table Number: " + orderRequest.getTableNumber());
            System.out.println("Customer: " + orderRequest.getCustomerFirstName() + " " + orderRequest.getCustomerLastName());
            System.out.println("Employee: " + orderRequest.getEmployeeId() + " - " + orderRequest.getEmployeeName());
            System.out.println("=====================================");
            
            Order order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO')")
    public ResponseEntity<List<Order>> getUserOrders() {
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
