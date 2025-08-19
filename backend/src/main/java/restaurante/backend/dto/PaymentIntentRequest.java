package restaurante.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PaymentIntentRequest {
    @NotNull
    private Long orderId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String currency = "USD";

    private String paymentMethodTypes = "card"; // card, paypal, etc.

    // Constructors
    public PaymentIntentRequest() {}

    public PaymentIntentRequest(Long orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethodTypes() {
        return paymentMethodTypes;
    }

    public void setPaymentMethodTypes(String paymentMethodTypes) {
        this.paymentMethodTypes = paymentMethodTypes;
    }
}
