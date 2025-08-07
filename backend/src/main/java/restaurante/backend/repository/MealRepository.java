package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Meal;
import restaurante.backend.entity.MealType;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByType(MealType type);
    List<Meal> findByNameContainingIgnoreCase(String name);
    List<Meal> findByAvailableTrue();
    List<Meal> findByTypeAndAvailableTrue(MealType type);
}
