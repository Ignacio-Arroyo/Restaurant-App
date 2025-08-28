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

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private EmailService emailService;

    public Order createOrder(OrderRequest orderRequest) {
        System.out.println("=== DEBUG: OrderService.createOrder called ===");
        System.out.println("OrderRequest received: " + (orderRequest != null ? "NOT NULL" : "NULL"));
        
        if (orderRequest != null) {
            System.out.println("- Meals count: " + (orderRequest.getMeals() != null ? orderRequest.getMeals().size() : "NULL"));
            System.out.println("- Drinks count: " + (orderRequest.getDrinks() != null ? orderRequest.getDrinks().size() : "NULL"));
            System.out.println("- Total cost: " + orderRequest.getTotalCost());
            System.out.println("- Order type: " + orderRequest.getOrderType());
            System.out.println("- Customer: " + orderRequest.getCustomerFirstName());
            System.out.println("- Employee ID: " + orderRequest.getEmployeeId());
        }
        
        Order order;
        
        // Si employeeId es "000000", es una orden de cliente directo (independientemente de si hay info de cliente)
        if ("000000".equals(orderRequest.getEmployeeId())) {
            System.out.println("DEBUG: Creating customer direct order (user-linked order)");
            // Orden de cliente autenticado - vincular al user
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("DEBUG: UserPrincipal username: " + userPrincipal.getUsername());
            
            User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userPrincipal.getUsername()));

            order = new Order(user, orderRequest.getTotalCost(), orderRequest.getOrderType());
            order.setTableNumber(orderRequest.getTableNumber());
            
            // Para órdenes de clientes directos, usar la información del usuario como cliente
            order.setCustomerFirstName(user.getFirstName());
            order.setCustomerLastName(user.getLastName());
            order.setCustomerPhone(""); // Los usuarios no tienen teléfono por ahora
            order.setEmployeeId("000000");
            order.setEmployeeName("Cliente Directo");
            order.setEmployeeRole("CUSTOMER");
        }
        // Si es una orden de empleado (tiene información del cliente y NO es employeeId 000000)
        else if (orderRequest.getCustomerFirstName() != null && !orderRequest.getCustomerFirstName().isEmpty()) {
            System.out.println("DEBUG: Creating employee order (local order)");
            // Crear orden local sin vincular a user
            order = new Order(null, orderRequest.getTotalCost(), orderRequest.getOrderType());
            order.setTableNumber(orderRequest.getTableNumber());
            
            // Configurar información del cliente
            order.setCustomerFirstName(orderRequest.getCustomerFirstName());
            order.setCustomerLastName(orderRequest.getCustomerLastName());
            order.setCustomerPhone(orderRequest.getCustomerPhone());
            order.setEmployeeId(orderRequest.getEmployeeId());
            order.setEmployeeName(orderRequest.getEmployeeName());
            order.setEmployeeRole(orderRequest.getEmployeeRole());
        } else {
            System.out.println("DEBUG: Creating regular user order (fallback)");
            // Fallback: Orden regular de usuario autenticado
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("DEBUG: UserPrincipal username: " + userPrincipal.getUsername());
            
            User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userPrincipal.getUsername()));

            order = new Order(user, orderRequest.getTotalCost(), orderRequest.getOrderType());
            order.setTableNumber(orderRequest.getTableNumber());
            
            // Para órdenes de clientes directos, usar la información del usuario como cliente
            order.setCustomerFirstName(user.getFirstName());
            order.setCustomerLastName(user.getLastName());
            order.setCustomerPhone(""); // Los usuarios no tienen teléfono por ahora
            order.setEmployeeId("000000");
            order.setEmployeeName("Cliente Directo");
            order.setEmployeeRole("CUSTOMER");
        }

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Verificar que hay al menos un producto en la orden
        boolean hasItems = false;

        // Process meals
        if (orderRequest.getMeals() != null && !orderRequest.getMeals().isEmpty()) {
            for (OrderRequest.OrderItemRequest mealRequest : orderRequest.getMeals()) {
                Meal meal = menuService.getMealById(mealRequest.getItemId());
                OrderMeal orderMeal = new OrderMeal(savedOrder, meal, mealRequest.getQuantity());
                savedOrder.getOrderMeals().add(orderMeal);
                hasItems = true;
            }
        }

        // Process drinks
        if (orderRequest.getDrinks() != null && !orderRequest.getDrinks().isEmpty()) {
            for (OrderRequest.OrderItemRequest drinkRequest : orderRequest.getDrinks()) {
                Drink drink = menuService.getDrinkById(drinkRequest.getItemId());
                OrderDrink orderDrink = new OrderDrink(savedOrder, drink, drinkRequest.getQuantity());
                savedOrder.getOrderDrinks().add(orderDrink);
                hasItems = true;
            }
        }

        // Process promotions if they exist
        if (orderRequest.getPromotions() != null && !orderRequest.getPromotions().isEmpty()) {
            for (OrderRequest.OrderItemRequest promotionRequest : orderRequest.getPromotions()) {
                Promotion promotion = menuService.getPromotionById(promotionRequest.getItemId());
                OrderPromotion orderPromotion = new OrderPromotion(savedOrder, promotion, promotionRequest.getQuantity(), promotion.getComboPrice());
                savedOrder.getOrderPromotions().add(orderPromotion);
                hasItems = true;
            }
        }

        // Validar que hay al menos un producto en la orden
        if (!hasItems) {
            throw new RuntimeException("Order must contain at least one item (meal, drink, or promotion)");
        }

        Order finalOrder = orderRepository.save(savedOrder);
        
        // Registrar venta automáticamente ya que la orden se considera pagada por defecto
        if (finalOrder.getPaid()) {
            registerSaleFromOrder(finalOrder);
        }
        
        // Enviar email de confirmación de orden solo si no es una orden de empleado
        // (porque el cliente puede no tener email registrado)
        if (finalOrder.getCustomerFirstName() == null || finalOrder.getCustomerFirstName().isEmpty()) {
            try {
                emailService.sendOrderConfirmationEmail(finalOrder);
            } catch (Exception e) {
                // Log del error pero no fallar la creación de la orden
                System.err.println("Error sending order confirmation email for order " + finalOrder.getId() + ": " + e.getMessage());
            }
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
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        // Procesar cuando el pedido esté listo
        if (status == OrderStatus.READY && previousStatus != OrderStatus.READY) {
            try {
                // Reducir inventario automáticamente
                inventoryService.processOrderCompletion(savedOrder);
                
                // Enviar email cuando el pedido esté listo
                emailService.sendOrderReadyEmail(savedOrder);
            } catch (Exception e) {
                // Log del error pero no fallar la actualización del estado
                System.err.println("Error processing order completion for order " + savedOrder.getId() + ": " + e.getMessage());
            }
        }
        
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
