package restaurante.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CouponValidationRequest {
    
    @NotBlank(message = "El código del cupón es obligatorio")
    private String couponCode;
    
    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto total debe ser mayor a 0")
    private BigDecimal totalAmount;
    
    // Constructor por defecto
    public CouponValidationRequest() {}
    
    // Constructor con parámetros
    public CouponValidationRequest(String couponCode, BigDecimal totalAmount) {
        this.couponCode = couponCode;
        this.totalAmount = totalAmount;
    }
    
    // Getters y Setters
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
