package restaurante.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "drink_inventory")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DrinkInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id", nullable = false)
    @NotNull
    private Drink drink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    @NotNull
    private InventoryItem inventoryItem;

    @NotNull
    @Positive
    @Column(name = "quantity_needed", precision = 10, scale = 2)
    private BigDecimal quantityNeeded; // Cantidad necesaria del ingrediente por porción de la bebida

    @Column(name = "notes")
    private String notes; // Notas sobre preparación, etc.

    // Constructors
    public DrinkInventory() {}

    public DrinkInventory(Drink drink, InventoryItem inventoryItem, BigDecimal quantityNeeded) {
        this.drink = drink;
        this.inventoryItem = inventoryItem;
        this.quantityNeeded = quantityNeeded;
    }

    public DrinkInventory(Drink drink, InventoryItem inventoryItem, BigDecimal quantityNeeded, String notes) {
        this.drink = drink;
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

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
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
