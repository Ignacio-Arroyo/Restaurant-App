package restaurante.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.MealWithInventoryDTO;
import restaurante.backend.entity.MealInventory;
import restaurante.backend.service.MealInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/meal-inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class MealInventoryController {

    private static final Logger logger = LoggerFactory.getLogger(MealInventoryController.class);

    @Autowired
    private MealInventoryService mealInventoryService;

    @GetMapping("/meal/{mealId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<MealInventory>> getMealInventoryRequirements(@PathVariable Long mealId) {
        try {
            List<MealInventory> requirements = mealInventoryService.getMealInventoryRequirements(mealId);
            return ResponseEntity.ok(requirements);
        } catch (Exception e) {
            logger.error("Error getting meal inventory requirements for meal {}: {}", mealId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/meal/{mealId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> updateMealInventoryRequirements(
            @PathVariable Long mealId,
            @RequestBody List<MealInventory> requirements) {
        try {
            mealInventoryService.updateMealInventoryRequirements(mealId, requirements);
            return ResponseEntity.ok("Requerimientos de inventario actualizados exitosamente");
        } catch (Exception e) {
            logger.error("Error updating meal inventory requirements for meal {}: {}", mealId, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/meal/{mealId}/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> batchUpdateMealInventory(
            @PathVariable Long mealId,
            @RequestBody MealWithInventoryDTO mealData) {
        try {
            mealInventoryService.batchUpdateMealInventory(mealId, mealData.getInventoryRequirements());
            return ResponseEntity.ok("Inventario de platillo actualizado exitosamente");
        } catch (Exception e) {
            logger.error("Error in batch update for meal {}: {}", mealId, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/meal/{mealId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> clearMealInventoryRequirements(@PathVariable Long mealId) {
        try {
            mealInventoryService.clearMealInventoryRequirements(mealId);
            return ResponseEntity.ok("Requerimientos de inventario eliminados exitosamente");
        } catch (Exception e) {
            logger.error("Error clearing meal inventory requirements for meal {}: {}", mealId, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/item/{inventoryItemId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<MealInventory>> getMealsUsingInventoryItem(@PathVariable Long inventoryItemId) {
        try {
            List<MealInventory> meals = mealInventoryService.getMealsUsingInventoryItem(inventoryItemId);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            logger.error("Error getting meals using inventory item {}: {}", inventoryItemId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
