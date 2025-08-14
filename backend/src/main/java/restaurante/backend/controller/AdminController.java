package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.DrinkDTO;
import restaurante.backend.dto.MealDTO;
import restaurante.backend.entity.Drink;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.Order;
import restaurante.backend.entity.OrderStatus;
import restaurante.backend.service.MenuService;
import restaurante.backend.service.OrderService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO')")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend"})
public class AdminController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    // Meal Management
    @PostMapping("/meals")
    public ResponseEntity<Meal> createMeal(@Valid @RequestBody MealDTO mealDTO) {
        try {
            Meal meal = new Meal();
            meal.setName(mealDTO.getName());
            meal.setDescription(mealDTO.getDescription());
            meal.setIngredients(mealDTO.getIngredients());
            meal.setPrice(mealDTO.getCost());
            meal.setType(mealDTO.getType());
            meal.setAllergens(mealDTO.getAllergens());
            meal.setAvailable(true); // Default to available
            meal.setImageUrl(mealDTO.getImageUrl()); // Set image URL
            
            Meal savedMeal = menuService.saveMeal(meal);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMeal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/meals/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @Valid @RequestBody MealDTO mealDTO) {
        Meal meal = menuService.getMealById(id);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        
        meal.setName(mealDTO.getName());
        meal.setDescription(mealDTO.getDescription());
        meal.setIngredients(mealDTO.getIngredients());
        meal.setCost(mealDTO.getCost());
        meal.setType(mealDTO.getType());
        meal.setAllergens(mealDTO.getAllergens());
        meal.setImageUrl(mealDTO.getImageUrl()); // Update image URL
        
        Meal updatedMeal = menuService.saveMeal(meal);
        return ResponseEntity.ok(updatedMeal);
    }

    @PutMapping("/meals/{id}/availability")
    public ResponseEntity<Meal> toggleMealAvailability(@PathVariable Long id) {
        Meal meal = menuService.getMealById(id);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        
        meal.setAvailable(!meal.isAvailable());
        Meal updatedMeal = menuService.saveMeal(meal);
        return ResponseEntity.ok(updatedMeal);
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        Meal meal = menuService.getMealById(id);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        
        menuService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    // Drink Management
    @PostMapping("/drinks")
    public ResponseEntity<Drink> createDrink(@Valid @RequestBody DrinkDTO drinkDTO) {
        try {
            Drink drink = new Drink();
            drink.setName(drinkDTO.getName());
            drink.setDescription(drinkDTO.getDescription());
            drink.setPrice(drinkDTO.getCost());
            drink.setType(drinkDTO.getType());
            drink.setSize(drinkDTO.getSize());
            drink.setAvailable(true); // Default to available
            drink.setImageUrl(drinkDTO.getImageUrl()); // Set image URL
            
            Drink savedDrink = menuService.saveDrink(drink);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDrink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/drinks/{id}")
    public ResponseEntity<Drink> updateDrink(@PathVariable Long id, @Valid @RequestBody DrinkDTO drinkDTO) {
        Drink drink = menuService.getDrinkById(id);
        if (drink == null) {
            return ResponseEntity.notFound().build();
        }
        
        drink.setName(drinkDTO.getName());
        drink.setDescription(drinkDTO.getDescription());
        drink.setPrice(drinkDTO.getCost());
        drink.setType(drinkDTO.getType());
        drink.setSize(drinkDTO.getSize());
        drink.setImageUrl(drinkDTO.getImageUrl()); // Update image URL
        
        Drink updatedDrink = menuService.saveDrink(drink);
        return ResponseEntity.ok(updatedDrink);
    }

    @PutMapping("/drinks/{id}/availability")
    public ResponseEntity<Drink> toggleDrinkAvailability(@PathVariable Long id) {
        Drink drink = menuService.getDrinkById(id);
        if (drink == null) {
            return ResponseEntity.notFound().build();
        }
        
        drink.setAvailable(!drink.isAvailable());
        Drink updatedDrink = menuService.saveDrink(drink);
        return ResponseEntity.ok(updatedDrink);
    }

    @DeleteMapping("/drinks/{id}")
    public ResponseEntity<Void> deleteDrink(@PathVariable Long id) {
        Drink drink = menuService.getDrinkById(id);
        if (drink == null) {
            return ResponseEntity.notFound().build();
        }
        
        menuService.deleteDrink(id);
        return ResponseEntity.noContent().build();
    }

    // Get all items for admin (including unavailable ones)
    @GetMapping("/meals")
    public ResponseEntity<List<Meal>> getAllMealsForAdmin() {
        List<Meal> meals = menuService.getAllMealsForAdmin();
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/drinks")
    public ResponseEntity<List<Drink>> getAllDrinksForAdmin() {
        List<Drink> drinks = menuService.getAllDrinksForAdmin();
        return ResponseEntity.ok(drinks);
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrdersForAdmin() {
        List<Order> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            OrderStatus status = OrderStatus.valueOf(statusRequest.get("status"));
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/orders/{id}/paid")
    public ResponseEntity<Order> updateOrderPaidStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> paidRequest) {
        try {
            Boolean paid = paidRequest.get("paid");
            Order updatedOrder = orderService.updateOrderPaidStatus(id, paid);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
