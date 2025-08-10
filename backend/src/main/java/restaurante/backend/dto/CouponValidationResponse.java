package restaurante.backend.dto;

import java.math.BigDecimal;

public class CouponValidationResponse {
    
    private boolean valid;
    private String message;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private CouponDTO coupon;
    
    // Constructor para cupón válido
    public CouponValidationResponse(boolean valid, String message, BigDecimal discountAmount, 
                                  BigDecimal finalAmount, CouponDTO coupon) {
        this.valid = valid;
        this.message = message;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.coupon = coupon;
    }
    
    // Constructor para cupón inválido
    public CouponValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.coupon = null;
    }
    
    // Constructor por defecto
    public CouponValidationResponse() {}
    
    // Métodos estáticos para crear respuestas comunes
    public static CouponValidationResponse success(BigDecimal discountAmount, BigDecimal finalAmount, 
                                                 CouponDTO coupon) {
        return new CouponValidationResponse(true, "Cupón aplicado exitosamente", 
                                          discountAmount, finalAmount, coupon);
    }
    
    public static CouponValidationResponse invalidCode() {
        return new CouponValidationResponse(false, "Código de cupón no encontrado");
    }
    
    public static CouponValidationResponse inactive() {
        return new CouponValidationResponse(false, "El cupón no está activo");
    }
    
    public static CouponValidationResponse minimumNotMet(BigDecimal minimumRequired) {
        return new CouponValidationResponse(false, 
            String.format("Se requiere una compra mínima de ₡%.2f", minimumRequired));
    }
    
    // Getters y Setters
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public CouponDTO getCoupon() {
        return coupon;
    }
    
    public void setCoupon(CouponDTO coupon) {
        this.coupon = coupon;
    }
}
