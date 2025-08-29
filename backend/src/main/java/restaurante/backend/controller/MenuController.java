package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.MealWithInventoryDTO;
import restaurante.backend.entity.Drink;
import restaurante.backend.entity.DrinkType;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.MealType;
import restaurante.backend.service.MenuService;
import restaurante.backend.service.MealInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MealInventoryService mealInventoryService;

    @GetMapping("/meals")
    public ResponseEntity<List<Meal>> getAllMeals() {
        return ResponseEntity.ok(menuService.getAllMeals());
    }

    @GetMapping("/meals/type/{type}")
    public ResponseEntity<List<Meal>> getMealsByType(@PathVariable MealType type) {
        return ResponseEntity.ok(menuService.getMealsByType(type));
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuService.getMealById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/drinks")
    public ResponseEntity<List<Drink>> getAllDrinks() {
        return ResponseEntity.ok(menuService.getAllDrinks());
    }

    @GetMapping("/drinks/type/{type}")
    public ResponseEntity<List<Drink>> getDrinksByType(@PathVariable DrinkType type) {
        return ResponseEntity.ok(menuService.getDrinksByType(type));
    }

    @GetMapping("/drinks/{id}")
    public ResponseEntity<Drink> getDrinkById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuService.getDrinkById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/meals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> createMealWithInventory(@RequestBody MealWithInventoryDTO mealData) {
        try {
            // Crear el platillo
            Meal meal = menuService.createMeal(mealData);
            
            // Si hay requerimientos de inventario, configurarlos
            if (mealData.getInventoryRequirements() != null && !mealData.getInventoryRequirements().isEmpty()) {
                mealInventoryService.batchUpdateMealInventory(meal.getId(), mealData.getInventoryRequirements());
            }
            
            return ResponseEntity.ok("Platillo creado exitosamente con ID: " + meal.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando platillo: " + e.getMessage());
        }
    }

    @PutMapping("/meals/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> updateMealWithInventory(@PathVariable Long id, @RequestBody MealWithInventoryDTO mealData) {
        try {
            // Actualizar el platillo
            menuService.updateMeal(id, mealData);
            
            // Actualizar requerimientos de inventario
            if (mealData.getInventoryRequirements() != null) {
                mealInventoryService.batchUpdateMealInventory(id, mealData.getInventoryRequirements());
            }
            
            return ResponseEntity.ok("Platillo actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error actualizando platillo: " + e.getMessage());
        }
    }

    @DeleteMapping("/meals/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> deleteMeal(@PathVariable Long id) {
        try {
            // Eliminar requerimientos de inventario primero
            mealInventoryService.clearMealInventoryRequirements(id);
            
            // Eliminar el platillo
            menuService.deleteMeal(id);
            
            return ResponseEntity.ok("Platillo eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando platillo: " + e.getMessage());
        }
    }
}
