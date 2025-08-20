package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Merma;
import restaurante.backend.entity.MermaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MermaRepository extends JpaRepository<Merma, Long> {
    
    // Buscar mermas por tipo
    List<Merma> findByType(MermaType type);
    
    // Buscar mermas por usuario que las registró
    List<Merma> findByRegisteredBy(String registeredBy);
    
    // Buscar mermas por rango de fechas
    List<Merma> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Buscar mermas por tipo y rango de fechas
    List<Merma> findByTypeAndRegisteredAtBetween(MermaType type, LocalDateTime start, LocalDateTime end);
    
    // Obtener costo total de mermas por rango de fechas
    @Query("SELECT COALESCE(SUM(m.totalCost), 0) FROM Merma m WHERE m.registeredAt BETWEEN :start AND :end")
    BigDecimal getTotalCostByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Obtener costo total de mermas por tipo y rango de fechas
    @Query("SELECT COALESCE(SUM(m.totalCost), 0) FROM Merma m WHERE m.type = :type AND m.registeredAt BETWEEN :start AND :end")
    BigDecimal getTotalCostByTypeAndDateRange(@Param("type") MermaType type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Obtener las mermas más recientes
    List<Merma> findTop10ByOrderByRegisteredAtDesc();
    
    // Buscar mermas por item específico
    List<Merma> findByTypeAndItemId(MermaType type, Long itemId);
    
    // Obtener estadísticas de mermas agrupadas por tipo
    @Query("SELECT m.type, COUNT(m), COALESCE(SUM(m.totalCost), 0) FROM Merma m WHERE m.registeredAt BETWEEN :start AND :end GROUP BY m.type")
    List<Object[]> getMermaStatsByType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Obtener items con más merma (por cantidad de registros)
    @Query("SELECT m.itemName, m.type, COUNT(m), COALESCE(SUM(m.totalCost), 0) FROM Merma m WHERE m.registeredAt BETWEEN :start AND :end GROUP BY m.itemName, m.type ORDER BY COUNT(m) DESC")
    List<Object[]> getItemsWithMostWaste(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
