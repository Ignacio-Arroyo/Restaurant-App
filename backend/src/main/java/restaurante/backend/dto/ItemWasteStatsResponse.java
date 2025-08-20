package restaurante.backend.dto;

import restaurante.backend.entity.MermaType;
import java.math.BigDecimal;

public class ItemWasteStatsResponse {
    private String itemName;
    private MermaType type;
    private Long wasteCount;
    private BigDecimal totalCost;

    public ItemWasteStatsResponse() {}

    public ItemWasteStatsResponse(String itemName, MermaType type, Long wasteCount, BigDecimal totalCost) {
        this.itemName = itemName;
        this.type = type;
        this.wasteCount = wasteCount;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public MermaType getType() {
        return type;
    }

    public void setType(MermaType type) {
        this.type = type;
    }

    public Long getWasteCount() {
        return wasteCount;
    }

    public void setWasteCount(Long wasteCount) {
        this.wasteCount = wasteCount;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
