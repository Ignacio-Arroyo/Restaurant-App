package restaurante.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private PromotionType type;
    
    // Para promociones de descuento
    @Positive
    @DecimalMax(value = "100.0", message = "El descuento no puede ser mayor a 100%")
    private BigDecimal discountPercentage;
    
    // Para promociones de combo
    @Positive
    private BigDecimal comboPrice;
    
    @ManyToMany
    @JoinTable(
        name = "promotion_meals",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    @JsonManagedReference
    private List<Meal> meals;
    
    @ManyToMany
    @JoinTable(
        name = "promotion_drinks",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "drink_id")
    )
    @JsonManagedReference
    private List<Drink> drinks;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum PromotionType {
        DISCOUNT,
        COMBO
    }
    
    // Constructors
    public Promotion() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Promotion(String name, String description, PromotionType type) {
        this();
        this.name = name;
        this.description = description;
        this.type = type;
    }
    
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
    
    public PromotionType getType() {
        return type;
    }
    
    public void setType(PromotionType type) {
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
    
    public List<Meal> getMeals() {
        return meals;
    }
    
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
    
    public List<Drink> getDrinks() {
        return drinks;
    }
    
    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Utility methods
    public BigDecimal calculateTotalPrice() {
        if (type == PromotionType.COMBO && comboPrice != null) {
            return comboPrice;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        if (meals != null) {
            for (Meal meal : meals) {
                total = total.add(meal.getCost());
            }
        }
        
        if (drinks != null) {
            for (Drink drink : drinks) {
                total = total.add(drink.getPrice());
            }
        }
        
        if (type == PromotionType.DISCOUNT && discountPercentage != null) {
            BigDecimal discount = total.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
            total = total.subtract(discount);
        }
        
        return total;
    }
    
    public BigDecimal calculateSavings() {
        if (type == PromotionType.COMBO && comboPrice != null) {
            BigDecimal originalTotal = BigDecimal.ZERO;
            
            if (meals != null) {
                for (Meal meal : meals) {
                    originalTotal = originalTotal.add(meal.getCost());
                }
            }
            
            if (drinks != null) {
                for (Drink drink : drinks) {
                    originalTotal = originalTotal.add(drink.getPrice());
                }
            }
            
            return originalTotal.subtract(comboPrice);
        }
        
        if (type == PromotionType.DISCOUNT && discountPercentage != null) {
            BigDecimal total = BigDecimal.ZERO;
            
            if (meals != null) {
                for (Meal meal : meals) {
                    total = total.add(meal.getCost());
                }
            }
            
            if (drinks != null) {
                for (Drink drink : drinks) {
                    total = total.add(drink.getPrice());
                }
            }
            
            return total.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }
        
        return BigDecimal.ZERO;
    }
}
