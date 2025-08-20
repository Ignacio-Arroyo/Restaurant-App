package restaurante.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import restaurante.backend.entity.MermaType;

import java.math.BigDecimal;

public class CreateMermaRequest {
    
    @NotNull
    private MermaType type;
    
    @NotNull
    private Long itemId;
    
    @NotNull
    @Positive
    private BigDecimal quantity;
    
    @Size(max = 500)
    private String reason;

    // Constructors
    public CreateMermaRequest() {}

    public CreateMermaRequest(MermaType type, Long itemId, BigDecimal quantity, String reason) {
        this.type = type;
        this.itemId = itemId;
        this.quantity = quantity;
        this.reason = reason;
    }

    // Getters and Setters
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
