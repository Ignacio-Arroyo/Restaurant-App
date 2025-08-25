package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.DrinkInventory;

import java.util.List;

@Repository
public interface DrinkInventoryRepository extends JpaRepository<DrinkInventory, Long> {
    
    List<DrinkInventory> findByDrinkId(Long drinkId);
    
    List<DrinkInventory> findByInventoryItemId(Long inventoryItemId);
    
    @Query("SELECT di FROM DrinkInventory di WHERE di.drink.id = :drinkId AND di.inventoryItem.active = true")
    List<DrinkInventory> findByDrinkIdAndActiveInventory(@Param("drinkId") Long drinkId);
    
    @Query("SELECT di FROM DrinkInventory di WHERE di.inventoryItem.id = :inventoryItemId AND di.drink.available = true")
    List<DrinkInventory> findByInventoryItemIdAndAvailableDrinks(@Param("inventoryItemId") Long inventoryItemId);
    
    @Query("SELECT CASE WHEN COUNT(di) > 0 THEN true ELSE false END " +
           "FROM DrinkInventory di WHERE di.drink.id = :drinkId AND " +
           "di.inventoryItem.currentStock < di.quantityNeeded")
    boolean hasDrinkInsufficientStock(@Param("drinkId") Long drinkId);
    
    @Query("SELECT di FROM DrinkInventory di WHERE di.drink.id = :drinkId AND " +
           "di.inventoryItem.currentStock < (di.quantityNeeded * :quantity)")
    List<DrinkInventory> findInsufficientStockForDrink(@Param("drinkId") Long drinkId, @Param("quantity") int quantity);
}
