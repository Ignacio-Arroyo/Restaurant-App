package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import restaurante.backend.dto.OrderRequest;
import restaurante.backend.entity.*;
import restaurante.backend.repository.OrderRepository;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SaleService saleService;

    public Order createOrder(OrderRequest orderRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order(user, orderRequest.getTotalCost(), orderRequest.getOrderType());
        order.setTableNumber(orderRequest.getTableNumber());

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Process meals
        for (OrderRequest.OrderItemRequest mealRequest : orderRequest.getMeals()) {
            Meal meal = menuService.getMealById(mealRequest.getItemId());
            OrderMeal orderMeal = new OrderMeal(savedOrder, meal, mealRequest.getQuantity());
            savedOrder.getOrderMeals().add(orderMeal);
        }

        // Process drinks
        for (OrderRequest.OrderItemRequest drinkRequest : orderRequest.getDrinks()) {
            Drink drink = menuService.getDrinkById(drinkRequest.getItemId());
            OrderDrink orderDrink = new OrderDrink(savedOrder, drink, drinkRequest.getQuantity());
            savedOrder.getOrderDrinks().add(orderDrink);
        }

        Order finalOrder = orderRepository.save(savedOrder);
        
        // Registrar venta automáticamente ya que la orden se considera pagada por defecto
        if (finalOrder.getPaid()) {
            registerSaleFromOrder(finalOrder);
        }
        
        return finalOrder;
    }

    public List<Order> getUserOrders() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    // Admin methods
    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        return savedOrder;
    }

    private void registerSaleFromOrder(Order order) {
        try {
            // Crear la venta principal
            String customerName = order.getUser() != null ? order.getUser().getEmail() : "Cliente Anónimo";
            Double totalAmount = order.getTotalCost().doubleValue();
            
            Sale sale = new Sale(customerName, totalAmount, LocalDateTime.now());
            
            // Crear los items de venta
            List<SaleItem> saleItems = new ArrayList<>();
            
            // Agregar meals
            if (order.getOrderMeals() != null) {
                for (OrderMeal orderMeal : order.getOrderMeals()) {
                    SaleItem item = new SaleItem(
                        sale,
                        orderMeal.getMeal().getName(),
                        "MEAL",
                        orderMeal.getQuantity(),
                        orderMeal.getMeal().getCost().doubleValue()
                    );
                    saleItems.add(item);
                }
            }
            
            // Agregar drinks
            if (order.getOrderDrinks() != null) {
                for (OrderDrink orderDrink : order.getOrderDrinks()) {
                    SaleItem item = new SaleItem(
                        sale,
                        orderDrink.getDrink().getName(),
                        "DRINK", 
                        orderDrink.getQuantity(),
                        orderDrink.getDrink().getPrice().doubleValue()
                    );
                    saleItems.add(item);
                }
            }
            
            sale.setItems(saleItems);
            saleService.createSale(sale);
            
        } catch (Exception e) {
            // Log el error pero no fallar la actualización del orden
            System.err.println("Error registrando venta para orden " + order.getId() + ": " + e.getMessage());
        }
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    public Order updateOrderPaidStatus(Long orderId, Boolean paid) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        Boolean previousPaidStatus = order.getPaid();
        order.setPaid(paid);
        Order savedOrder = orderRepository.save(order);
        
        // Si cambia de no pagado a pagado, registrar la venta
        if (paid && !previousPaidStatus) {
            registerSaleFromOrder(savedOrder);
        }
        
        return savedOrder;
    }
}
