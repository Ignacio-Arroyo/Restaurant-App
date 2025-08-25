package restaurante.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "meal_inventory")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MealInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    @NotNull
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    @NotNull
    private InventoryItem inventoryItem;

    @NotNull
    @Positive
    @Column(name = "quantity_needed", precision = 10, scale = 2)
    private BigDecimal quantityNeeded; // Cantidad necesaria del ingrediente por porción del platillo

    @Column(name = "notes")
    private String notes; // Notas sobre preparación, etc.

    // Constructors
    public MealInventory() {}

    public MealInventory(Meal meal, InventoryItem inventoryItem, BigDecimal quantityNeeded) {
        this.meal = meal;
        this.inventoryItem = inventoryItem;
        this.quantityNeeded = quantityNeeded;
    }

    public MealInventory(Meal meal, InventoryItem inventoryItem, BigDecimal quantityNeeded, String notes) {
        this.meal = meal;
        this.inventoryItem = inventoryItem;
        this.quantityNeeded = quantityNeeded;
        this.notes = notes;
    }

    // Business methods
    public BigDecimal getTotalQuantityNeeded(int portions) {
        return quantityNeeded.multiply(BigDecimal.valueOf(portions));
    }

    public boolean isIngredientAvailable(int portions) {
        BigDecimal totalNeeded = getTotalQuantityNeeded(portions);
        return inventoryItem.getCurrentStock().compareTo(totalNeeded) >= 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(BigDecimal quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
