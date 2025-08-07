package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import restaurante.backend.dto.OrderRequest;
import restaurante.backend.entity.*;
import restaurante.backend.repository.OrderRepository;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.security.UserPrincipal;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuService menuService;

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

        return orderRepository.save(savedOrder);
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
}
