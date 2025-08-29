package restaurante.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.entity.*;
import restaurante.backend.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private MealInventoryRepository mealInventoryRepository;

    @Autowired
    private DrinkInventoryRepository drinkInventoryRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    // ==================== INVENTORY ITEM MANAGEMENT ====================

    public List<InventoryItem> getAllActiveItems() {
        return inventoryItemRepository.findByActiveTrueOrderByNameAsc();
    }

    public Page<InventoryItem> searchItems(String searchTerm, Pageable pageable) {
        return inventoryItemRepository.findBySearchTerm(searchTerm, pageable);
    }

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    public InventoryItem saveItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(id);
        if (item.isPresent()) {
            item.get().setActive(false);
            inventoryItemRepository.save(item.get());
        }
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryItemRepository.findLowStockItems();
    }

    public List<InventoryItem> getOutOfStockItems() {
        return inventoryItemRepository.findOutOfStockItems();
    }

    public List<InventoryItem> getItemsByCategory(InventoryCategory category) {
        return inventoryItemRepository.findByCategoryAndActiveTrue(category);
    }

    // ==================== MEAL INVENTORY MANAGEMENT ====================

    public List<MealInventory> getMealIngredients(Long mealId) {
        return mealInventoryRepository.findByMealIdAndActiveInventory(mealId);
    }

    public MealInventory saveMealIngredient(MealInventory mealInventory) {
        return mealInventoryRepository.save(mealInventory);
    }

    public void deleteMealIngredient(Long id) {
        mealInventoryRepository.deleteById(id);
    }

    // ==================== DRINK INVENTORY MANAGEMENT ====================

    public List<DrinkInventory> getDrinkIngredients(Long drinkId) {
        return drinkInventoryRepository.findByDrinkIdAndActiveInventory(drinkId);
    }

    public DrinkInventory saveDrinkIngredient(DrinkInventory drinkInventory) {
        return drinkInventoryRepository.save(drinkInventory);
    }

    public void deleteDrinkIngredient(Long id) {
        drinkInventoryRepository.deleteById(id);
    }

    // ==================== STOCK VALIDATION ====================

    public boolean isMealAvailable(Long mealId, int quantity) {
        List<MealInventory> insufficientItems = mealInventoryRepository
                .findInsufficientStockForMeal(mealId, quantity);
        return insufficientItems.isEmpty();
    }

    public boolean isDrinkAvailable(Long drinkId, int quantity) {
        List<DrinkInventory> insufficientItems = drinkInventoryRepository
                .findInsufficientStockForDrink(drinkId, quantity);
        return insufficientItems.isEmpty();
    }

    public boolean isPromotionAvailable(Long promotionId, int quantity) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (!promotionOpt.isPresent()) {
            return false;
        }

        Promotion promotion = promotionOpt.get();
        
        // Verificar disponibilidad de platillos en la promoción
        if (promotion.getMeals() != null) {
            for (Meal meal : promotion.getMeals()) {
                if (!isMealAvailable(meal.getId(), quantity)) {
                    return false;
                }
            }
        }

        // Verificar disponibilidad de bebidas en la promoción
        if (promotion.getDrinks() != null) {
            for (Drink drink : promotion.getDrinks()) {
                if (!isDrinkAvailable(drink.getId(), quantity)) {
                    return false;
                }
            }
        }

        return true;
    }

    // ==================== STOCK REDUCTION ====================

    @Transactional
    public void reduceStockForMeal(Long mealId, int quantity) {
        List<MealInventory> mealIngredients = getMealIngredients(mealId);
        
        for (MealInventory mealInventory : mealIngredients) {
            InventoryItem inventoryItem = mealInventory.getInventoryItem();
            BigDecimal totalNeeded = mealInventory.getTotalQuantityNeeded(quantity);
            
            logger.info("Reduciendo stock: {} - {} {} (tenía: {}, reduce: {})", 
                       inventoryItem.getName(), 
                       totalNeeded, 
                       inventoryItem.getUnit(),
                       inventoryItem.getCurrentStock(),
                       totalNeeded);
            
            inventoryItem.reduceStock(totalNeeded);
            inventoryItemRepository.save(inventoryItem);
        }
    }

    @Transactional
    public void reduceStockForDrink(Long drinkId, int quantity) {
        List<DrinkInventory> drinkIngredients = getDrinkIngredients(drinkId);
        
        for (DrinkInventory drinkInventory : drinkIngredients) {
            InventoryItem inventoryItem = drinkInventory.getInventoryItem();
            BigDecimal totalNeeded = drinkInventory.getTotalQuantityNeeded(quantity);
            
            logger.info("Reduciendo stock: {} - {} {} (tenía: {}, reduce: {})", 
                       inventoryItem.getName(), 
                       totalNeeded, 
                       inventoryItem.getUnit(),
                       inventoryItem.getCurrentStock(),
                       totalNeeded);
            
            inventoryItem.reduceStock(totalNeeded);
            inventoryItemRepository.save(inventoryItem);
        }
    }

    @Transactional
    public void reduceStockForPromotion(Long promotionId, int quantity) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (!promotionOpt.isPresent()) {
            logger.warn("Promoción no encontrada: {}", promotionId);
            return;
        }

        Promotion promotion = promotionOpt.get();
        
        // Reducir stock de platillos en la promoción
        if (promotion.getMeals() != null) {
            for (Meal meal : promotion.getMeals()) {
                reduceStockForMeal(meal.getId(), quantity);
            }
        }

        // Reducir stock de bebidas en la promoción
        if (promotion.getDrinks() != null) {
            for (Drink drink : promotion.getDrinks()) {
                reduceStockForDrink(drink.getId(), quantity);
            }
        }
    }

    // ==================== ORDER PROCESSING ====================

    @Transactional
    public void processOrderCompletion(Order order) {
        logger.info("Procesando finalización de orden #{} - reduciendo inventario", order.getId());

        // Procesar platillos
        if (order.getOrderMeals() != null) {
            for (OrderMeal orderMeal : order.getOrderMeals()) {
                reduceStockForMeal(orderMeal.getMeal().getId(), orderMeal.getQuantity());
            }
        }

        // Procesar bebidas
        if (order.getOrderDrinks() != null) {
            for (OrderDrink orderDrink : order.getOrderDrinks()) {
                reduceStockForDrink(orderDrink.getDrink().getId(), orderDrink.getQuantity());
            }
        }

        // Procesar promociones
        if (order.getOrderPromotions() != null) {
            for (OrderPromotion orderPromotion : order.getOrderPromotions()) {
                reduceStockForPromotion(orderPromotion.getPromotion().getId(), orderPromotion.getQuantity());
            }
        }

        logger.info("Inventario actualizado para orden #{}", order.getId());
    }

    // ==================== STOCK RESTORATION ====================

    @Transactional
    public void restoreStockFromCancelledOrder(Order order) {
        logger.info("Restaurando inventario para orden cancelada #{}", order.getId());

        // Restaurar platillos
        if (order.getOrderMeals() != null) {
            for (OrderMeal orderMeal : order.getOrderMeals()) {
                restoreStockForMeal(orderMeal.getMeal().getId(), orderMeal.getQuantity());
            }
        }

        // Restaurar bebidas
        if (order.getOrderDrinks() != null) {
            for (OrderDrink orderDrink : order.getOrderDrinks()) {
                restoreStockForDrink(orderDrink.getDrink().getId(), orderDrink.getQuantity());
            }
        }

        // Restaurar promociones
        if (order.getOrderPromotions() != null) {
            for (OrderPromotion orderPromotion : order.getOrderPromotions()) {
                restoreStockForPromotion(orderPromotion.getPromotion().getId(), orderPromotion.getQuantity());
            }
        }

        logger.info("Inventario restaurado para orden cancelada #{}", order.getId());
    }

    @Transactional
    public void restoreStockForMeal(Long mealId, int quantity) {
        List<MealInventory> mealIngredients = getMealIngredients(mealId);
        
        for (MealInventory mealInventory : mealIngredients) {
            InventoryItem inventoryItem = mealInventory.getInventoryItem();
            BigDecimal totalToRestore = mealInventory.getTotalQuantityNeeded(quantity);
            
            logger.info("Restaurando stock: {} + {} {} (tenía: {}, nuevo: {})", 
                       inventoryItem.getName(), 
                       totalToRestore, 
                       inventoryItem.getUnit(),
                       inventoryItem.getCurrentStock(),
                       inventoryItem.getCurrentStock().add(totalToRestore));
            
            inventoryItem.addStock(totalToRestore);
            inventoryItemRepository.save(inventoryItem);
        }
    }

    @Transactional
    public void restoreStockForDrink(Long drinkId, int quantity) {
        List<DrinkInventory> drinkIngredients = getDrinkIngredients(drinkId);
        
        for (DrinkInventory drinkInventory : drinkIngredients) {
            InventoryItem inventoryItem = drinkInventory.getInventoryItem();
            BigDecimal totalToRestore = drinkInventory.getTotalQuantityNeeded(quantity);
            
            logger.info("Restaurando stock: {} + {} {} (tenía: {}, nuevo: {})", 
                       inventoryItem.getName(), 
                       totalToRestore, 
                       inventoryItem.getUnit(),
                       inventoryItem.getCurrentStock(),
                       inventoryItem.getCurrentStock().add(totalToRestore));
            
            inventoryItem.addStock(totalToRestore);
            inventoryItemRepository.save(inventoryItem);
        }
    }

    @Transactional
    public void restoreStockForPromotion(Long promotionId, int quantity) {
        // Obtener la promoción y sus componentes
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (promotionOpt.isEmpty()) {
            logger.warn("Promoción no encontrada: {}", promotionId);
            return;
        }

        Promotion promotion = promotionOpt.get();
        
        // Restaurar stock de las comidas incluidas
        if (promotion.getMeals() != null) {
            for (Meal meal : promotion.getMeals()) {
                restoreStockForMeal(meal.getId(), quantity);
            }
        }

        // Restaurar stock de las bebidas incluidas  
        if (promotion.getDrinks() != null) {
            for (Drink drink : promotion.getDrinks()) {
                restoreStockForDrink(drink.getId(), quantity);
            }
        }
    }

    // ==================== STOCK ALERTS ====================

    public List<InventoryItem> generateStockAlerts() {
        return getLowStockItems();
    }

    public boolean hasLowStockAlert() {
        return !getLowStockItems().isEmpty();
    }
}
