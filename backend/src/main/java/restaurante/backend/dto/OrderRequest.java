package restaurante.backend.dto;

import restaurante.backend.entity.OrderType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {
    // Estos campos pueden estar vacíos si solo hay promociones
    private List<OrderItemRequest> meals;
    
    private List<OrderItemRequest> drinks;
    
    private List<OrderItemRequest> promotions; // Promociones son opcionales
    
    @NotNull
    @Positive
    private BigDecimal totalCost;
    
    @NotNull
    private OrderType orderType;
    
    private Integer tableNumber; // Only for DINE_IN orders
    
    // Campos para órdenes creadas por empleados
    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;
    private String employeeId;
    private String employeeName;
    private String employeeRole;

    // Constructors
    public OrderRequest() {}

    // Getters and Setters
    public List<OrderItemRequest> getMeals() {
        return meals;
    }

    public void setMeals(List<OrderItemRequest> meals) {
        this.meals = meals;
    }

    public List<OrderItemRequest> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<OrderItemRequest> drinks) {
        this.drinks = drinks;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItemRequest> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<OrderItemRequest> promotions) {
        this.promotions = promotions;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    // Inner class for order items
    public static class OrderItemRequest {
        @NotNull
        private Long itemId;
        
        @NotNull
        @Positive
        private Integer quantity;

        // Constructors
        public OrderItemRequest() {}

        public OrderItemRequest(Long itemId, Integer quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
