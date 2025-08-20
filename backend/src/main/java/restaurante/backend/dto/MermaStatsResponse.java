package restaurante.backend.dto;

import restaurante.backend.entity.MermaType;
import java.math.BigDecimal;

public class MermaStatsResponse {
    private MermaType type;
    private Long count;
    private BigDecimal totalCost;

    public MermaStatsResponse() {}

    public MermaStatsResponse(MermaType type, Long count, BigDecimal totalCost) {
        this.type = type;
        this.count = count;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public MermaType getType() {
        return type;
    }

    public void setType(MermaType type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
