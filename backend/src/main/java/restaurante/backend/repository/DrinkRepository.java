package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Drink;
import restaurante.backend.entity.DrinkType;

import java.util.List;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findByType(DrinkType type);
    List<Drink> findByNameContainingIgnoreCase(String name);
    List<Drink> findByAvailableTrue();
    List<Drink> findByTypeAndAvailableTrue(DrinkType type);
}
