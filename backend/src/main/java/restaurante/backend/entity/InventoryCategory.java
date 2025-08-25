package restaurante.backend.entity;

public enum InventoryCategory {
    VEGETABLES("Verduras"),
    FRUITS("Frutas"),
    MEAT("Carnes"),
    FISH("Pescados"),
    DAIRY("Lácteos"),
    GRAINS("Granos"),
    SPICES("Especias"),
    BEVERAGES("Bebidas"),
    ALCOHOL("Alcohol"),
    OILS("Aceites"),
    CONDIMENTS("Condimentos"),
    FROZEN("Congelados"),
    CANNED("Enlatados"),
    BAKERY("Panadería"),
    CLEANING("Limpieza"),
    PACKAGING("Empaques"),
    OTHER("Otros");

    private final String displayName;

    InventoryCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
