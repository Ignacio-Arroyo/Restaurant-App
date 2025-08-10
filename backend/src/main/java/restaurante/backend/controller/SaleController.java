package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.ProductStatsDTO;
import restaurante.backend.dto.SalesReportDTO;
import restaurante.backend.entity.Sale;
import restaurante.backend.service.SaleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:3000")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
        try {
            Sale savedSale = saleService.createSale(sale);
            return ResponseEntity.ok(savedSale);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Sale>> getAllSales() {
        try {
            List<Sale> sales = saleService.getAllSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        try {
            Optional<Sale> sale = saleService.getSaleById(id);
            return sale.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Sale>> getTodaySales() {
        try {
            List<Sale> sales = saleService.getTodaySales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Sale>> getRecentSales() {
        try {
            List<Sale> sales = saleService.getRecentSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            SalesReportDTO report = saleService.getSalesReport(start, end);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductStatsDTO>> getProductStats(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String type) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<ProductStatsDTO> stats;
            if (type != null && !type.trim().isEmpty()) {
                stats = saleService.getProductStatsByType(start, end, type.toUpperCase());
            } else {
                // Si no se especifica tipo, obtener el reporte completo y extraer los productos
                SalesReportDTO report = saleService.getSalesReport(start, end);
                stats = report.getProductStats();
            }
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/most-sold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductStatsDTO>> getMostSoldProducts(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<ProductStatsDTO> stats = saleService.getMostSoldProducts(start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalRevenue(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            Double totalRevenue = saleService.calculateTotalRevenueBetweenDates(start, end);
            return ResponseEntity.ok(totalRevenue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
