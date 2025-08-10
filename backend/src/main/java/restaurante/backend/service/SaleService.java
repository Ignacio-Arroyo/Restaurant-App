package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.dto.ProductStatsDTO;
import restaurante.backend.dto.SalesReportDTO;
import restaurante.backend.entity.Sale;
import restaurante.backend.entity.SaleItem;
import restaurante.backend.repository.SaleRepository;
import restaurante.backend.repository.SaleItemRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Transactional
    public Sale createSale(Sale sale) {
        // Establecer la fecha actual si no está establecida
        if (sale.getSaleDate() == null) {
            sale.setSaleDate(LocalDateTime.now());
        }
        
        Sale savedSale = saleRepository.save(sale);
        
        // Asociar los items con la venta guardada
        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                item.setSale(savedSale);
                saleItemRepository.save(item);
            }
        }
        
        return savedSale;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }

    public List<Sale> getSalesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }

    public List<Sale> getTodaySales() {
        return saleRepository.findTodaySales();
    }

    public List<Sale> getRecentSales() {
        return saleRepository.findTop10ByOrderBySaleDateDesc();
    }

    public SalesReportDTO getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Obtener todas las ventas en el rango
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        
        // Calcular totales
        Double totalRevenue = saleRepository.calculateTotalSalesBetweenDates(startDate, endDate);
        if (totalRevenue == null) totalRevenue = 0.0;
        
        Integer totalSales = sales.size();
        
        // Obtener estadísticas de productos
        List<Object[]> productStatsRaw = saleItemRepository.getProductStatsBetweenDates(startDate, endDate);
        
        List<ProductStatsDTO> productStats = productStatsRaw.stream()
                .map(row -> new ProductStatsDTO(
                        (String) row[0],           // productName
                        (String) row[1],           // productType
                        ((Number) row[2]).longValue(), // totalQuantity
                        ((Number) row[3]).doubleValue() // totalRevenue
                ))
                .collect(Collectors.toList());
        
        return new SalesReportDTO(startDate, endDate, totalRevenue, totalSales, productStats);
    }

    public List<ProductStatsDTO> getProductStatsByType(LocalDateTime startDate, LocalDateTime endDate, String productType) {
        List<Object[]> statsRaw = saleItemRepository.getProductStatsByTypeBetweenDates(startDate, endDate, productType);
        
        return statsRaw.stream()
                .map(row -> new ProductStatsDTO(
                        (String) row[0],           // productName
                        productType,               // productType
                        ((Number) row[1]).longValue(), // totalQuantity
                        ((Number) row[2]).doubleValue() // totalRevenue
                ))
                .collect(Collectors.toList());
    }

    public List<ProductStatsDTO> getMostSoldProducts(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> statsRaw = saleItemRepository.getMostSoldProductsBetweenDates(startDate, endDate);
        
        return statsRaw.stream()
                .map(row -> new ProductStatsDTO(
                        (String) row[0],           // productName
                        (String) row[1],           // productType
                        ((Number) row[2]).longValue(), // totalQuantity
                        null                       // totalRevenue (no incluido en esta consulta)
                ))
                .collect(Collectors.toList());
    }

    public Double calculateTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Double total = saleRepository.calculateTotalSalesBetweenDates(startDate, endDate);
        return total != null ? total : 0.0;
    }
}
