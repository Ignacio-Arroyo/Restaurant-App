package restaurante.backend.dto;

public class ProductStatsDTO {
    private String productName;
    private String productType;
    private Long totalQuantity;
    private Double totalRevenue;

    // Constructor por defecto
    public ProductStatsDTO() {}

    // Constructor con parámetros
    public ProductStatsDTO(String productName, String productType, Long totalQuantity, Double totalRevenue) {
        this.productName = productName;
        this.productType = productType;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters y Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
