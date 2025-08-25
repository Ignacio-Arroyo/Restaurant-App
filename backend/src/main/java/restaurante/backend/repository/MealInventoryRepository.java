package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.MealInventory;

import java.util.List;

@Repository
public interface MealInventoryRepository extends JpaRepository<MealInventory, Long> {
    
    List<MealInventory> findByMealId(Long mealId);
    
    List<MealInventory> findByInventoryItemId(Long inventoryItemId);
    
    @Query("SELECT mi FROM MealInventory mi WHERE mi.meal.id = :mealId AND mi.inventoryItem.active = true")
    List<MealInventory> findByMealIdAndActiveInventory(@Param("mealId") Long mealId);
    
    @Query("SELECT mi FROM MealInventory mi WHERE mi.inventoryItem.id = :inventoryItemId AND mi.meal.available = true")
    List<MealInventory> findByInventoryItemIdAndAvailableMeals(@Param("inventoryItemId") Long inventoryItemId);
    
    @Query("SELECT CASE WHEN COUNT(mi) > 0 THEN true ELSE false END " +
           "FROM MealInventory mi WHERE mi.meal.id = :mealId AND " +
           "mi.inventoryItem.currentStock < mi.quantityNeeded")
    boolean hasMealInsufficientStock(@Param("mealId") Long mealId);
    
    @Query("SELECT mi FROM MealInventory mi WHERE mi.meal.id = :mealId AND " +
           "mi.inventoryItem.currentStock < (mi.quantityNeeded * :quantity)")
    List<MealInventory> findInsufficientStockForMeal(@Param("mealId") Long mealId, @Param("quantity") int quantity);
}
