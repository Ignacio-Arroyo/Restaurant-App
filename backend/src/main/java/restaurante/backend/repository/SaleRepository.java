package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Sale;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    // Encontrar ventas en un rango de fechas
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Encontrar ventas por cliente
    List<Sale> findByCustomerNameContainingIgnoreCase(String customerName);
    
    // Calcular el total de ventas en un rango de fechas
    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Double calculateTotalSalesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // Obtener las últimas N ventas
    List<Sale> findTop10ByOrderBySaleDateDesc();
    
    // Encontrar ventas de hoy
    @Query(value = "SELECT * FROM sales WHERE DATE(sale_date) = CURRENT_DATE", nativeQuery = true)
    List<Sale> findTodaySales();
}
