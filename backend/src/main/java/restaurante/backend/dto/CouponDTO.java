package restaurante.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import restaurante.backend.entity.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del cupón es obligatorio")
    private String name;
    
    @NotBlank(message = "El código del cupón es obligatorio")
    @Size(min = 10, max = 50, message = "El código debe tener entre 10 y 50 caracteres")
    private String code;
    
    private Boolean active = true;
    
    @NotNull(message = "El tipo de descuento es obligatorio")
    private Coupon.DiscountType discountType;
    
    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor del descuento debe ser mayor a 0")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.0", message = "La compra mínima debe ser mayor o igual a 0")
    private BigDecimal minimumPurchase;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructor por defecto
    public CouponDTO() {}
    
    // Constructor desde entidad
    public CouponDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.code = coupon.getCode();
        this.active = coupon.getActive();
        this.discountType = coupon.getDiscountType();
        this.discountValue = coupon.getDiscountValue();
        this.minimumPurchase = coupon.getMinimumPurchase();
        this.createdAt = coupon.getCreatedAt();
        this.updatedAt = coupon.getUpdatedAt();
    }
    
    // Método para convertir a entidad
    public Coupon toEntity() {
        Coupon coupon = new Coupon();
        coupon.setId(this.id);
        coupon.setName(this.name);
        coupon.setCode(this.code);
        coupon.setActive(this.active);
        coupon.setDiscountType(this.discountType);
        coupon.setDiscountValue(this.discountValue);
        coupon.setMinimumPurchase(this.minimumPurchase);
        return coupon;
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
        this.code = code;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Coupon.DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(Coupon.DiscountType discountType) {
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
}
