package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.SaleItem;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    // Obtener estadísticas de productos vendidos en un rango de fechas
    @Query("SELECT si.productName, si.productType, SUM(si.quantity) as totalQuantity, SUM(si.totalPrice) as totalRevenue " +
           "FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY si.productName, si.productType " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> getProductStatsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    // Obtener los productos más vendidos por cantidad
    @Query("SELECT si.productName, si.productType, SUM(si.quantity) as totalQuantity " +
           "FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY si.productName, si.productType " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> getMostSoldProductsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    // Obtener productos por tipo (MEAL o DRINK)
    @Query("SELECT si.productName, SUM(si.quantity) as totalQuantity, SUM(si.totalPrice) as totalRevenue " +
           "FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate AND si.productType = :productType " +
           "GROUP BY si.productName " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> getProductStatsByTypeBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("productType") String productType);
}
