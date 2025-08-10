package restaurante.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SalesReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalRevenue;
    private Integer totalSales;
    private List<ProductStatsDTO> productStats;

    // Constructor por defecto
    public SalesReportDTO() {}

    // Constructor con parámetros
    public SalesReportDTO(LocalDateTime startDate, LocalDateTime endDate, Double totalRevenue, 
                         Integer totalSales, List<ProductStatsDTO> productStats) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue;
        this.totalSales = totalSales;
        this.productStats = productStats;
    }

    // Getters y Setters
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public List<ProductStatsDTO> getProductStats() {
        return productStats;
    }

    public void setProductStats(List<ProductStatsDTO> productStats) {
        this.productStats = productStats;
    }
}
