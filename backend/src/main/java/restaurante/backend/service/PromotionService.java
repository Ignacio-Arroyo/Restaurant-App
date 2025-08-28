package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.dto.PromotionDTO;
import restaurante.backend.entity.Promotion;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.Drink;
import restaurante.backend.repository.PromotionRepository;
import restaurante.backend.repository.MealRepository;
import restaurante.backend.repository.DrinkRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private DrinkRepository drinkRepository;
    
    // Obtener todas las promociones para admin (temporalmente solo COMBO)
    public List<PromotionDTO> getAllPromotionsForAdmin() {
        List<Promotion> promotions = promotionRepository.findAllByOrderByCreatedAtDesc();
        return promotions.stream()
                .filter(promotion -> promotion.getType() == Promotion.PromotionType.COMBO)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener todas las promociones activas (para clientes) - temporalmente solo COMBO
    public List<PromotionDTO> getAllActivePromotions() {
        List<Promotion> promotions = promotionRepository.findByAvailableTrueOrderByCreatedAtDesc();
        return promotions.stream()
                .filter(promotion -> promotion.getType() == Promotion.PromotionType.COMBO)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener promoción por ID
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    // Crear nueva promoción
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        validatePromotionDTO(promotionDTO);
        
        Promotion promotion = new Promotion();
        updatePromotionFromDTO(promotion, promotionDTO);
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }
    
    // Actualizar promoción
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        validatePromotionDTO(promotionDTO);
        
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        
        updatePromotionFromDTO(promotion, promotionDTO);
        
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return convertToDTO(updatedPromotion);
    }
    
    // Alternar disponibilidad de promoción
    public PromotionDTO togglePromotionAvailability(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        
        promotion.setAvailable(!promotion.getAvailable());
        
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return convertToDTO(updatedPromotion);
    }
    
    // Eliminar promoción
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promoción no encontrada");
        }
        promotionRepository.deleteById(id);
    }
    
    // Buscar promociones por nombre
    public List<PromotionDTO> searchPromotions(String name) {
        List<Promotion> promotions = promotionRepository.findByNameContainingIgnoreCase(name);
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener promociones por tipo (temporalmente solo COMBO)
    public List<PromotionDTO> getPromotionsByType(String type) {
        try {
            Promotion.PromotionType promotionType = Promotion.PromotionType.valueOf(type.toUpperCase());
            // Temporalmente solo devolver promociones de tipo COMBO
            if (promotionType != Promotion.PromotionType.COMBO) {
                return List.of(); // Lista vacía para tipos que no sean COMBO
            }
            List<Promotion> promotions = promotionRepository.findByTypeAndAvailableTrue(promotionType);
            return promotions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de promoción inválido: " + type);
        }
    }
    
    // Métodos auxiliares
    private void validatePromotionDTO(PromotionDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la promoción es obligatorio");
        }
        
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new RuntimeException("El tipo de promoción es obligatorio");
        }
        
        try {
            Promotion.PromotionType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de promoción inválido: " + dto.getType());
        }
        
        // Temporalmente rechazar promociones de descuento
        if ("DISCOUNT".equals(dto.getType().toUpperCase())) {
            throw new RuntimeException("Las promociones de descuento están temporalmente desactivadas. Solo se permiten combos.");
        }
        
        if ("COMBO".equals(dto.getType().toUpperCase())) {
            if (dto.getComboPrice() == null || dto.getComboPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El precio del combo es obligatorio para promociones de combo");
            }
            if ((dto.getMealIds() == null || dto.getMealIds().isEmpty()) && 
                (dto.getDrinkIds() == null || dto.getDrinkIds().isEmpty())) {
                throw new RuntimeException("Los combos deben incluir al menos un producto");
            }
        }
    }
    
    private void updatePromotionFromDTO(Promotion promotion, PromotionDTO dto) {
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setType(Promotion.PromotionType.valueOf(dto.getType().toUpperCase()));
        promotion.setImageUrl(dto.getImageUrl());
        
        if (dto.getAvailable() != null) {
            promotion.setAvailable(dto.getAvailable());
        }
        
        if ("DISCOUNT".equals(dto.getType().toUpperCase())) {
            promotion.setDiscountPercentage(dto.getDiscountPercentage());
            promotion.setComboPrice(null);
            promotion.setMeals(null);
            promotion.setDrinks(null);
        } else if ("COMBO".equals(dto.getType().toUpperCase())) {
            promotion.setComboPrice(dto.getComboPrice());
            promotion.setDiscountPercentage(null);
            
            // Cargar meals
            if (dto.getMealIds() != null && !dto.getMealIds().isEmpty()) {
                List<Meal> meals = mealRepository.findAllById(dto.getMealIds());
                promotion.setMeals(meals);
            } else {
                promotion.setMeals(null);
            }
            
            // Cargar drinks
            if (dto.getDrinkIds() != null && !dto.getDrinkIds().isEmpty()) {
                List<Drink> drinks = drinkRepository.findAllById(dto.getDrinkIds());
                promotion.setDrinks(drinks);
            } else {
                promotion.setDrinks(null);
            }
        }
    }
    
    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setType(promotion.getType().toString());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setComboPrice(promotion.getComboPrice());
        dto.setAvailable(promotion.getAvailable());
        dto.setImageUrl(promotion.getImageUrl());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());
        
        // Convertir meals a DTOs
        if (promotion.getMeals() != null) {
            List<PromotionDTO.MealSummaryDTO> mealDTOs = promotion.getMeals().stream()
                    .map(meal -> new PromotionDTO.MealSummaryDTO(meal.getId(), meal.getName(), meal.getCost()))
                    .collect(Collectors.toList());
            dto.setMeals(mealDTOs);
        }
        
        // Convertir drinks a DTOs
        if (promotion.getDrinks() != null) {
            List<PromotionDTO.DrinkSummaryDTO> drinkDTOs = promotion.getDrinks().stream()
                    .map(drink -> new PromotionDTO.DrinkSummaryDTO(drink.getId(), drink.getName(), drink.getPrice()))
                    .collect(Collectors.toList());
            dto.setDrinks(drinkDTOs);
        }
        
        // Calcular precios
        dto.setTotalPrice(promotion.calculateTotalPrice());
        dto.setSavings(promotion.calculateSavings());
        
        return dto;
    }
}
