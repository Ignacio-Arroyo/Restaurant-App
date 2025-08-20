package restaurante.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mermas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Merma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MermaType type; // PRODUCT, MEAL, DRINK

    @Column(name = "item_id")
    private Long itemId; // ID del producto, meal o drink

    @Column(name = "item_name")
    @Size(max = 100)
    private String itemName; // Nombre del item para referencia

    @NotNull
    @Positive
    private BigDecimal quantity; // Cantidad desperdiciada

    @Size(max = 50)
    private String unit; // Unidad de medida

    @NotNull
    private BigDecimal unitCost; // Costo unitario del item

    @Column(name = "total_cost")
    private BigDecimal totalCost; // Costo total de la merma (quantity * unitCost)

    @Column(columnDefinition = "TEXT")
    private String reason; // Razón del desperdicio

    @Column(name = "registered_by")
    private String registeredBy; // Email del usuario que registró

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    // Constructors
    public Merma() {
        this.registeredAt = LocalDateTime.now();
    }

    public Merma(MermaType type, Long itemId, String itemName, BigDecimal quantity, 
                 String unit, BigDecimal unitCost, String reason, String registeredBy) {
        this.type = type;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitCost = unitCost;
        this.totalCost = quantity.multiply(unitCost);
        this.reason = reason;
        this.registeredBy = registeredBy;
        this.registeredAt = LocalDateTime.now();
    }

    // Method to calculate total cost
    public void calculateTotalCost() {
        if (quantity != null && unitCost != null) {
            this.totalCost = quantity.multiply(unitCost);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MermaType getType() {
        return type;
    }

    public void setType(MermaType type) {
        this.type = type;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        calculateTotalCost();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
        calculateTotalCost();
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        calculateTotalCost();
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }
}
