package restaurante.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.InventoryCategory;
import restaurante.backend.entity.InventoryItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    List<InventoryItem> findByActiveTrue();
    
    List<InventoryItem> findByActiveTrueOrderByNameAsc();
    
    List<InventoryItem> findByCategory(InventoryCategory category);
    
    List<InventoryItem> findByCategoryAndActiveTrue(InventoryCategory category);
    
    Optional<InventoryItem> findByNameAndActiveTrue(String name);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND i.currentStock <= i.minimumStock")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND i.currentStock <= :threshold")
    List<InventoryItem> findItemsBelowStock(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND i.currentStock = 0")
    List<InventoryItem> findOutOfStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<InventoryItem> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND i.supplier = :supplier")
    List<InventoryItem> findBySupplier(@Param("supplier") String supplier);
    
    @Query("SELECT DISTINCT i.supplier FROM InventoryItem i WHERE i.active = true AND i.supplier IS NOT NULL")
    List<String> findAllSuppliers();
}
