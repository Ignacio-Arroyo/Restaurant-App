package restaurante.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.dto.MealWithInventoryDTO;
import restaurante.backend.entity.InventoryItem;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.MealInventory;
import restaurante.backend.repository.InventoryItemRepository;
import restaurante.backend.repository.MealInventoryRepository;
import restaurante.backend.repository.MealRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class MealInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(MealInventoryService.class);

    @Autowired
    private MealInventoryRepository mealInventoryRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    /**
     * Obtiene los requerimientos de inventario para un platillo específico
     */
    public List<MealInventory> getMealInventoryRequirements(Long mealId) {
        logger.info("Obteniendo requerimientos de inventario para meal ID: {}", mealId);
        return mealInventoryRepository.findByMealIdAndActiveInventory(mealId);
    }

    /**
     * Actualiza los requerimientos de inventario para un platillo
     */
    @Transactional
    public void updateMealInventoryRequirements(Long mealId, List<MealInventory> requirements) {
        logger.info("Actualizando requerimientos de inventario para meal ID: {}", mealId);
        
        // Verificar que el platillo existe
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Platillo no encontrado con ID: " + mealId));

        // Eliminar requerimientos existentes
        clearMealInventoryRequirements(mealId);

        // Agregar nuevos requerimientos
        for (MealInventory requirement : requirements) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(requirement.getInventoryItem().getId())
                    .orElseThrow(() -> new RuntimeException("Item de inventario no encontrado: " + requirement.getInventoryItem().getId()));

            MealInventory mealInventory = new MealInventory();
            mealInventory.setMeal(meal);
            mealInventory.setInventoryItem(inventoryItem);
            mealInventory.setQuantityNeeded(requirement.getQuantityNeeded());

            mealInventoryRepository.save(mealInventory);
        }

        logger.info("Requerimientos de inventario actualizados para meal ID: {}", mealId);
    }

    /**
     * Actualización en lote usando DTO
     */
    @Transactional
    public void batchUpdateMealInventory(Long mealId, List<MealWithInventoryDTO.MealInventoryRequestDTO> requirements) {
        logger.info("Actualizando inventario en lote para meal ID: {}", mealId);
        
        // Verificar que el platillo existe
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Platillo no encontrado con ID: " + mealId));

        // Eliminar requerimientos existentes
        clearMealInventoryRequirements(mealId);

        // Agregar nuevos requerimientos
        for (MealWithInventoryDTO.MealInventoryRequestDTO requirement : requirements) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(requirement.getInventoryItemId())
                    .orElseThrow(() -> new RuntimeException("Item de inventario no encontrado: " + requirement.getInventoryItemId()));

            MealInventory mealInventory = new MealInventory();
            mealInventory.setMeal(meal);
            mealInventory.setInventoryItem(inventoryItem);
            mealInventory.setQuantityNeeded(BigDecimal.valueOf(requirement.getQuantityNeeded()));

            mealInventoryRepository.save(mealInventory);
        }

        logger.info("Inventario en lote actualizado para meal ID: {}", mealId);
    }

    /**
     * Elimina todos los requerimientos de inventario para un platillo
     */
    @Transactional
    public void clearMealInventoryRequirements(Long mealId) {
        logger.info("Eliminando requerimientos de inventario para meal ID: {}", mealId);
        List<MealInventory> existingRequirements = mealInventoryRepository.findByMealId(mealId);
        mealInventoryRepository.deleteAll(existingRequirements);
    }

    /**
     * Obtiene los platillos que usan un item específico del inventario
     */
    public List<MealInventory> getMealsUsingInventoryItem(Long inventoryItemId) {
        logger.info("Obteniendo platillos que usan inventory item ID: {}", inventoryItemId);
        return mealInventoryRepository.findByInventoryItemIdAndAvailableMeals(inventoryItemId);
    }

    /**
     * Verifica si un platillo tiene configuración de inventario
     */
    public boolean mealHasInventoryConfiguration(Long mealId) {
        List<MealInventory> requirements = getMealInventoryRequirements(mealId);
        return !requirements.isEmpty();
    }

    /**
     * Calcula el costo estimado de ingredientes para un platillo
     */
    public BigDecimal calculateEstimatedIngredientCost(Long mealId) {
        List<MealInventory> requirements = getMealInventoryRequirements(mealId);
        
        return requirements.stream()
                .filter(req -> req.getInventoryItem().getCostPerUnit() != null)
                .map(req -> req.getQuantityNeeded().multiply(req.getInventoryItem().getCostPerUnit()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Valida que todos los items de inventario especificados existen y están activos
     */
    public void validateInventoryItems(List<MealWithInventoryDTO.MealInventoryRequestDTO> requirements) {
        for (MealWithInventoryDTO.MealInventoryRequestDTO requirement : requirements) {
            Optional<InventoryItem> item = inventoryItemRepository.findById(requirement.getInventoryItemId());
            
            if (item.isEmpty()) {
                throw new RuntimeException("Item de inventario no encontrado: " + requirement.getInventoryItemId());
            }
            
            if (!item.get().isActive()) {
                throw new RuntimeException("Item de inventario inactivo: " + item.get().getName());
            }
            
            if (requirement.getQuantityNeeded() <= 0) {
                throw new RuntimeException("La cantidad requerida debe ser mayor a 0 para: " + item.get().getName());
            }
        }
    }
}
