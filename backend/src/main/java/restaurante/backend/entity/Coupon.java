package restaurante.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Size(min = 10, max = 50)
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.0")
    @Column(precision = 10, scale = 2)
    private BigDecimal minimumPurchase;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Enum para tipo de descuento
    public enum DiscountType {
        PERCENTAGE, // Descuento en porcentaje
        FIXED       // Descuento en cantidad fija
    }
    
    // Constructor por defecto
    public Coupon() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor con parámetros
    public Coupon(String name, String code, DiscountType discountType, BigDecimal discountValue) {
        this();
        this.name = name;
        this.code = code.toUpperCase(); // Convertir a mayúsculas
        this.discountType = discountType;
        this.discountValue = discountValue;
    }
    
    // Getters y Setters
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
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase() : null;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    
    public BigDecimal getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }
    
    public BigDecimal getMinimumPurchase() {
        return minimumPurchase;
    }
    
    public void setMinimumPurchase(BigDecimal minimumPurchase) {
        this.minimumPurchase = minimumPurchase;
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
    
    // Método para actualizar timestamp
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Método para calcular el descuento aplicado
    public BigDecimal calculateDiscount(BigDecimal totalAmount) {
        if (!active) {
            return BigDecimal.ZERO;
        }
        
        // Verificar compra mínima
        if (minimumPurchase != null && totalAmount.compareTo(minimumPurchase) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            // Descuento porcentual
            discount = totalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            // Descuento fijo
            discount = discountValue;
        }
        
        // El descuento no puede ser mayor al total
        return discount.min(totalAmount);
    }
    
    // Método para validar si el cupón es aplicable
    public boolean isApplicable(BigDecimal totalAmount) {
        if (!active) {
            return false;
        }
        
        if (minimumPurchase != null && totalAmount.compareTo(minimumPurchase) < 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active=" + active +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", minimumPurchase=" + minimumPurchase +
                '}';
    }
}
