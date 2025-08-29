package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurante.backend.dto.MealWithInventoryDTO;
import restaurante.backend.entity.Drink;
import restaurante.backend.entity.DrinkType;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.MealType;
import restaurante.backend.entity.Promotion;
import restaurante.backend.repository.DrinkRepository;
import restaurante.backend.repository.MealRepository;
import restaurante.backend.repository.PromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    // Customer endpoints - only show available items
    public List<Meal> getAllMeals() {
        return mealRepository.findByAvailableTrue();
    }

    public List<Meal> getMealsByType(MealType type) {
        return mealRepository.findByTypeAndAvailableTrue(type);
    }

    public List<Drink> getAllDrinks() {
        return drinkRepository.findByAvailableTrue();
    }

    public List<Drink> getDrinksByType(DrinkType type) {
        return drinkRepository.findByTypeAndAvailableTrue(type);
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));
    }

    public Drink getDrinkById(Long id) {
        return drinkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Drink not found with id: " + id));
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
    }

    // Admin methods for CRUD operations
    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    public Drink saveDrink(Drink drink) {
        return drinkRepository.save(drink);
    }

    public void deleteMeal(Long id) {
        mealRepository.deleteById(id);
    }

    public void deleteDrink(Long id) {
        drinkRepository.deleteById(id);
    }

    // Get all items for admin (including unavailable ones)
    public List<Meal> getAllMealsForAdmin() {
        return mealRepository.findAll();
    }

    public List<Drink> getAllDrinksForAdmin() {
        return drinkRepository.findAll();
    }

    // ==================== ADVANCED MEAL MANAGEMENT ====================

    public Meal createMeal(MealWithInventoryDTO mealData) {
        Meal meal = new Meal();
        meal.setName(mealData.getName());
        meal.setDescription(mealData.getDescription());
        meal.setIngredients(mealData.getIngredients());
        meal.setCost(BigDecimal.valueOf(mealData.getCost()));
        meal.setType(mealData.getType());
        meal.setAllergens(mealData.getAllergens());
        meal.setImageUrl(mealData.getImageUrl());
        meal.setAvailable(mealData.getAvailable() != null ? mealData.getAvailable() : true);
        
        return mealRepository.save(meal);
    }

    public void updateMeal(Long id, MealWithInventoryDTO mealData) {
        Meal meal = getMealById(id);
        
        meal.setName(mealData.getName());
        meal.setDescription(mealData.getDescription());
        meal.setIngredients(mealData.getIngredients());
        meal.setCost(BigDecimal.valueOf(mealData.getCost()));
        meal.setType(mealData.getType());
        meal.setAllergens(mealData.getAllergens());
        meal.setImageUrl(mealData.getImageUrl());
        if (mealData.getAvailable() != null) {
            meal.setAvailable(mealData.getAvailable());
        }
        
        mealRepository.save(meal);
    }
}
