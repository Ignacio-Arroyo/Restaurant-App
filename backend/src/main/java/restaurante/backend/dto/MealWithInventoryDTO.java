package restaurante.backend.dto;

import restaurante.backend.entity.MealType;
import java.util.List;

public class MealWithInventoryDTO {
    private String name;
    private String description;
    private String ingredients;
    private Double cost;
    private MealType type;
    private String allergens;
    private String imageUrl;
    private Boolean available;
    private List<MealInventoryRequestDTO> inventoryRequirements;

    // Constructors
    public MealWithInventoryDTO() {}

    public MealWithInventoryDTO(String name, String description, String ingredients, 
                               Double cost, MealType type, String allergens, String imageUrl) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.cost = cost;
        this.type = type;
        this.allergens = allergens;
        this.imageUrl = imageUrl;
        this.available = true;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<MealInventoryRequestDTO> getInventoryRequirements() {
        return inventoryRequirements;
    }

    public void setInventoryRequirements(List<MealInventoryRequestDTO> inventoryRequirements) {
        this.inventoryRequirements = inventoryRequirements;
    }

    // Inner DTO class for inventory requirements
    public static class MealInventoryRequestDTO {
        private Long inventoryItemId;
        private Double quantityNeeded;

        public MealInventoryRequestDTO() {}

        public MealInventoryRequestDTO(Long inventoryItemId, Double quantityNeeded) {
            this.inventoryItemId = inventoryItemId;
            this.quantityNeeded = quantityNeeded;
        }

        public Long getInventoryItemId() {
            return inventoryItemId;
        }

        public void setInventoryItemId(Long inventoryItemId) {
            this.inventoryItemId = inventoryItemId;
        }

        public Double getQuantityNeeded() {
            return quantityNeeded;
        }

        public void setQuantityNeeded(Double quantityNeeded) {
            this.quantityNeeded = quantityNeeded;
        }
    }
}
