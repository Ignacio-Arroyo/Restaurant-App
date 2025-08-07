package restaurante.backend.dto;

import restaurante.backend.entity.OrderType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {
    @NotNull
    private List<OrderItemRequest> meals;
    
    @NotNull
    private List<OrderItemRequest> drinks;
    
    @NotNull
    @Positive
    private BigDecimal totalCost;
    
    @NotNull
    private OrderType orderType;
    
    private Integer tableNumber; // Only for DINE_IN orders

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
