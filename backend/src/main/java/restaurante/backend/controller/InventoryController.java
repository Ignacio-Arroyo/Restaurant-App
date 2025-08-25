package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.entity.*;
import restaurante.backend.service.InventoryService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/inventory")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ==================== INVENTORY ITEMS ====================

    @GetMapping("/items")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        List<InventoryItem> items = inventoryService.getAllActiveItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Page<InventoryItem>> searchItems(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> items = inventoryService.searchItems(searchTerm, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<InventoryItem> getItem(@PathVariable Long id) {
        Optional<InventoryItem> item = inventoryService.getItemById(id);
        return item.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<InventoryItem> createItem(@Valid @RequestBody InventoryItem item) {
        InventoryItem savedItem = inventoryService.saveItem(item);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<InventoryItem> updateItem(@PathVariable Long id, @Valid @RequestBody InventoryItem item) {
        item.setId(id);
        InventoryItem updatedItem = inventoryService.saveItem(item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<InventoryItem>> getItemsByCategory(@PathVariable InventoryCategory category) {
        List<InventoryItem> items = inventoryService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    // ==================== STOCK ALERTS ====================

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        List<InventoryItem> items = inventoryService.getLowStockItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/out-of-stock")
    public ResponseEntity<List<InventoryItem>> getOutOfStockItems() {
        List<InventoryItem> items = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/has-alerts")
    public ResponseEntity<Boolean> hasStockAlerts() {
        boolean hasAlerts = inventoryService.hasLowStockAlert();
        return ResponseEntity.ok(hasAlerts);
    }

    // ==================== MEAL INVENTORY ====================

    @GetMapping("/meals/{mealId}/ingredients")
    public ResponseEntity<List<MealInventory>> getMealIngredients(@PathVariable Long mealId) {
        List<MealInventory> ingredients = inventoryService.getMealIngredients(mealId);
        return ResponseEntity.ok(ingredients);
    }

    @PostMapping("/meals/{mealId}/ingredients")
    public ResponseEntity<MealInventory> addMealIngredient(
            @PathVariable Long mealId,
            @Valid @RequestBody MealInventory mealInventory) {
        MealInventory savedIngredient = inventoryService.saveMealIngredient(mealInventory);
        return ResponseEntity.ok(savedIngredient);
    }

    @PutMapping("/meals/ingredients/{id}")
    public ResponseEntity<MealInventory> updateMealIngredient(
            @PathVariable Long id,
            @Valid @RequestBody MealInventory mealInventory) {
        mealInventory.setId(id);
        MealInventory updatedIngredient = inventoryService.saveMealIngredient(mealInventory);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/meals/ingredients/{id}")
    public ResponseEntity<Void> deleteMealIngredient(@PathVariable Long id) {
        inventoryService.deleteMealIngredient(id);
        return ResponseEntity.ok().build();
    }

    // ==================== DRINK INVENTORY ====================

    @GetMapping("/drinks/{drinkId}/ingredients")
    public ResponseEntity<List<DrinkInventory>> getDrinkIngredients(@PathVariable Long drinkId) {
        List<DrinkInventory> ingredients = inventoryService.getDrinkIngredients(drinkId);
        return ResponseEntity.ok(ingredients);
    }

    @PostMapping("/drinks/{drinkId}/ingredients")
    public ResponseEntity<DrinkInventory> addDrinkIngredient(
            @PathVariable Long drinkId,
            @Valid @RequestBody DrinkInventory drinkInventory) {
        DrinkInventory savedIngredient = inventoryService.saveDrinkIngredient(drinkInventory);
        return ResponseEntity.ok(savedIngredient);
    }

    @PutMapping("/drinks/ingredients/{id}")
    public ResponseEntity<DrinkInventory> updateDrinkIngredient(
            @PathVariable Long id,
            @Valid @RequestBody DrinkInventory drinkInventory) {
        drinkInventory.setId(id);
        DrinkInventory updatedIngredient = inventoryService.saveDrinkIngredient(drinkInventory);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/drinks/ingredients/{id}")
    public ResponseEntity<Void> deleteDrinkIngredient(@PathVariable Long id) {
        inventoryService.deleteDrinkIngredient(id);
        return ResponseEntity.ok().build();
    }

    // ==================== AVAILABILITY CHECKS ====================

    @GetMapping("/availability/meal/{mealId}")
    public ResponseEntity<Boolean> checkMealAvailability(
            @PathVariable Long mealId,
            @RequestParam(defaultValue = "1") int quantity) {
        boolean available = inventoryService.isMealAvailable(mealId, quantity);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/availability/drink/{drinkId}")
    public ResponseEntity<Boolean> checkDrinkAvailability(
            @PathVariable Long drinkId,
            @RequestParam(defaultValue = "1") int quantity) {
        boolean available = inventoryService.isDrinkAvailable(drinkId, quantity);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/availability/promotion/{promotionId}")
    public ResponseEntity<Boolean> checkPromotionAvailability(
            @PathVariable Long promotionId,
            @RequestParam(defaultValue = "1") int quantity) {
        boolean available = inventoryService.isPromotionAvailable(promotionId, quantity);
        return ResponseEntity.ok(available);
    }

    // ==================== CATEGORIES ====================

    @GetMapping("/categories")
    public ResponseEntity<InventoryCategory[]> getCategories() {
        return ResponseEntity.ok(InventoryCategory.values());
    }
}
