package restaurante.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PromotionDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    private String description;
    
    @NotNull(message = "El tipo de promoción es obligatorio")
    private String type; // "DISCOUNT" o "COMBO"
    
    @Positive(message = "El porcentaje de descuento debe ser positivo")
    @DecimalMax(value = "100.0", message = "El descuento no puede ser mayor a 100%")
    private BigDecimal discountPercentage;
    
    @Positive(message = "El precio del combo debe ser positivo")
    private BigDecimal comboPrice;
    
    private List<Long> mealIds;
    private List<Long> drinkIds;
    
    private Boolean available;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Para respuestas con datos completos
    private List<MealSummaryDTO> meals;
    private List<DrinkSummaryDTO> drinks;
    private BigDecimal totalPrice;
    private BigDecimal savings;
    
    // Constructors
    public PromotionDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public BigDecimal getComboPrice() {
        return comboPrice;
    }
    
    public void setComboPrice(BigDecimal comboPrice) {
        this.comboPrice = comboPrice;
    }
    
    public List<Long> getMealIds() {
        return mealIds;
    }
    
    public void setMealIds(List<Long> mealIds) {
        this.mealIds = mealIds;
    }
    
    public List<Long> getDrinkIds() {
        return drinkIds;
    }
    
    public void setDrinkIds(List<Long> drinkIds) {
        this.drinkIds = drinkIds;
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<MealSummaryDTO> getMeals() {
        return meals;
    }
    
    public void setMeals(List<MealSummaryDTO> meals) {
        this.meals = meals;
    }
    
    public List<DrinkSummaryDTO> getDrinks() {
        return drinks;
    }
    
    public void setDrinks(List<DrinkSummaryDTO> drinks) {
        this.drinks = drinks;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BigDecimal getSavings() {
        return savings;
    }
    
    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }
    
    // DTOs internos para resúmenes
    public static class MealSummaryDTO {
        private Long id;
        private String name;
        private BigDecimal cost;
        
        public MealSummaryDTO() {}
        
        public MealSummaryDTO(Long id, String name, BigDecimal cost) {
            this.id = id;
            this.name = name;
            this.cost = cost;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
    }
    
    public static class DrinkSummaryDTO {
        private Long id;
        private String name;
        private BigDecimal price;
        
        public DrinkSummaryDTO() {}
        
        public DrinkSummaryDTO(Long id, String name, BigDecimal price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
