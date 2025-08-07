package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.entity.Drink;
import restaurante.backend.entity.DrinkType;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.MealType;
import restaurante.backend.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuController {

    @Autowired
    private MenuService menuService;

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
}
