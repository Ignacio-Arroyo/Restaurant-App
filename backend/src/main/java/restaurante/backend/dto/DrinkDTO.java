package restaurante.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import restaurante.backend.entity.DrinkType;

import java.math.BigDecimal;

public class DrinkDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;
    private String ingredients;
    private String size;

    @NotNull
    @Positive
    private BigDecimal cost;

    @NotNull
    private DrinkType type;

    // Constructors
    public DrinkDTO() {}

    public DrinkDTO(String name, String description, String ingredients, String size, BigDecimal cost, DrinkType type) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.size = size;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public DrinkType getType() {
        return type;
    }

    public void setType(DrinkType type) {
        this.type = type;
    }
}
