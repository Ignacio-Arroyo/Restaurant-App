package restaurante.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import restaurante.backend.entity.MealType;

import java.math.BigDecimal;

public class MealDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;
    private String ingredients;
    private String allergens;

    @NotNull
    @Positive
    private BigDecimal cost;

    @NotNull
    private MealType type;

    // Constructors
    public MealDTO() {}

    public MealDTO(String name, String description, String ingredients, String allergens, BigDecimal cost, MealType type) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.cost = cost;
        this.type = type;
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

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }
}
