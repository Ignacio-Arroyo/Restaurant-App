package restaurante.backend.dto;

import restaurante.backend.entity.Payment;

public class PaymentIntentResponse {
    private Long paymentId;
    private String clientSecret;
    private String stripePaymentIntentId;
    private Payment.PaymentStatus status;
    private String message;

    // Constructors
    public PaymentIntentResponse() {}

    public PaymentIntentResponse(Long paymentId, String clientSecret, String stripePaymentIntentId) {
        this.paymentId = paymentId;
        this.clientSecret = clientSecret;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.status = Payment.PaymentStatus.PENDING;
    }

    public PaymentIntentResponse(String message) {
        this.message = message;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public Payment.PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
