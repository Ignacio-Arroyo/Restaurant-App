package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Promotion;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    // Encontrar promociones activas
    List<Promotion> findByAvailableTrue();
    
    // Encontrar promociones por tipo
    List<Promotion> findByType(Promotion.PromotionType type);
    
    // Encontrar promociones activas por tipo
    List<Promotion> findByTypeAndAvailableTrue(Promotion.PromotionType type);
    
    // Buscar promociones por nombre (case insensitive)
    List<Promotion> findByNameContainingIgnoreCase(String name);
    
    // Buscar promociones activas por nombre
    List<Promotion> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
    
    // Contar promociones activas
    long countByAvailableTrue();
    
    // Contar promociones por tipo
    long countByType(Promotion.PromotionType type);
    
    // Encontrar promociones ordenadas por fecha de creación
    List<Promotion> findAllByOrderByCreatedAtDesc();
    
    // Encontrar promociones activas ordenadas por fecha de creación
    List<Promotion> findByAvailableTrueOrderByCreatedAtDesc();
    
    // Query personalizada para promociones con comidas específicas
    @Query("SELECT DISTINCT p FROM Promotion p JOIN p.meals m WHERE m.id = :mealId AND p.available = true")
    List<Promotion> findPromotionsWithMeal(@Param("mealId") Long mealId);
    
    // Query personalizada para promociones con bebidas específicas
    @Query("SELECT DISTINCT p FROM Promotion p JOIN p.drinks d WHERE d.id = :drinkId AND p.available = true")
    List<Promotion> findPromotionsWithDrink(@Param("drinkId") Long drinkId);
}
