package restaurante.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

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

        // Verificar disponibilidad de inventario ANTES de crear la orden
        if (orderRequest.getMeals() != null && !orderRequest.getMeals().isEmpty()) {
            for (OrderRequest.OrderItemRequest mealRequest : orderRequest.getMeals()) {
                if (!inventoryService.isMealAvailable(mealRequest.getItemId(), mealRequest.getQuantity())) {
                    throw new RuntimeException("No hay suficiente inventario para el platillo solicitado: " + mealRequest.getItemId());
                }
            }
        }
        
        if (orderRequest.getDrinks() != null && !orderRequest.getDrinks().isEmpty()) {
            for (OrderRequest.OrderItemRequest drinkRequest : orderRequest.getDrinks()) {
                if (!inventoryService.isDrinkAvailable(drinkRequest.getItemId(), drinkRequest.getQuantity())) {
                    throw new RuntimeException("No hay suficiente inventario para la bebida solicitada: " + drinkRequest.getItemId());
                }
            }
        }

        if (orderRequest.getPromotions() != null && !orderRequest.getPromotions().isEmpty()) {
            for (OrderRequest.OrderItemRequest promotionRequest : orderRequest.getPromotions()) {
                if (!inventoryService.isPromotionAvailable(promotionRequest.getItemId(), promotionRequest.getQuantity())) {
                    throw new RuntimeException("No hay suficiente inventario para la promoción solicitada: " + promotionRequest.getItemId());
                }
            }
        }

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
        
        // RESERVAR INVENTARIO INMEDIATAMENTE al crear la orden
        try {
            inventoryService.processOrderCompletion(finalOrder);
            System.out.println("Inventario reservado para orden #" + finalOrder.getId());
        } catch (Exception e) {
            // Si falla la reserva de inventario, cancelar la orden
            orderRepository.delete(finalOrder);
            throw new RuntimeException("Error al reservar inventario: " + e.getMessage());
        }
        
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

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        OrderStatus previousStatus = order.getStatus();
        
        // Validar transiciones de estado válidas
        if (!isValidStatusTransition(previousStatus, status)) {
            throw new RuntimeException("Transición de estado inválida: " + previousStatus + " -> " + status);
        }
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        // Si se cancela la orden, restaurar inventario
        if (status == OrderStatus.CANCELLED) {
            inventoryService.restoreStockFromCancelledOrder(order);
            logger.info("Inventario restaurado para orden cancelada #{}", orderId);
        }
        
        // Procesar cuando el pedido esté listo
        if (status == OrderStatus.READY && previousStatus != OrderStatus.READY) {
            try {
                // Solo enviar email cuando el pedido esté listo
                // El inventario ya se redujo al crear la orden
                emailService.sendOrderReadyEmail(savedOrder);
                System.out.println("Email de pedido listo enviado para orden #" + savedOrder.getId());
            } catch (Exception e) {
                // Log del error pero no fallar la actualización del estado
                System.err.println("Error sending ready email for order " + savedOrder.getId() + ": " + e.getMessage());
            }
        }
        
        return savedOrder;
    }

    // Validar transiciones de estado válidas
    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        if (from == to) return true;
        
        return switch (from) {
            case PENDING -> to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING -> to == OrderStatus.READY || to == OrderStatus.CANCELLED;
            case READY -> to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
            case DELIVERED -> false; // No se puede cambiar desde delivered
            case CANCELLED -> false; // No se puede cambiar desde cancelled
            default -> false; // Estados desconocidos no permiten transiciones
        };
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
