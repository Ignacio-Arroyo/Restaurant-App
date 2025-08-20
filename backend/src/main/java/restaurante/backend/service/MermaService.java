package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.dto.CreateMermaRequest;
import restaurante.backend.dto.ItemWasteStatsResponse;
import restaurante.backend.dto.MermaStatsResponse;
import restaurante.backend.entity.*;
import restaurante.backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MermaService {

    @Autowired
    private MermaRepository mermaRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private DrinkRepository drinkRepository;

    // Crear nueva merma
    public Merma createMerma(CreateMermaRequest request, String userEmail) {
        Merma merma = new Merma();
        merma.setType(request.getType());
        merma.setItemId(request.getItemId());
        merma.setQuantity(request.getQuantity());
        merma.setReason(request.getReason());
        merma.setRegisteredBy(userEmail);

        // Obtener información del item según el tipo
        switch (request.getType()) {
            case PRODUCT:
                Optional<Product> product = productRepository.findById(request.getItemId());
                if (product.isPresent()) {
                    Product p = product.get();
                    merma.setItemName(p.getName());
                    merma.setUnitCost(p.getCost());
                    merma.setUnit(p.getUnit());
                    
                    // Actualizar cantidad en inventario
                    BigDecimal newQuantity = BigDecimal.valueOf(p.getQuantity()).subtract(request.getQuantity());
                    if (newQuantity.compareTo(BigDecimal.ZERO) >= 0) {
                        p.setQuantity(newQuantity.intValue());
                        productRepository.save(p);
                    } else {
                        throw new RuntimeException("Cantidad insuficiente en inventario");
                    }
                } else {
                    throw new RuntimeException("Producto no encontrado");
                }
                break;
                
            case MEAL:
                Optional<Meal> meal = mealRepository.findById(request.getItemId());
                if (meal.isPresent()) {
                    Meal m = meal.get();
                    merma.setItemName(m.getName());
                    merma.setUnitCost(m.getCost());
                    merma.setUnit("unidad");
                } else {
                    throw new RuntimeException("Platillo no encontrado");
                }
                break;
                
            case DRINK:
                Optional<Drink> drink = drinkRepository.findById(request.getItemId());
                if (drink.isPresent()) {
                    Drink d = drink.get();
                    merma.setItemName(d.getName());
                    merma.setUnitCost(d.getCost());
                    merma.setUnit("unidad");
                } else {
                    throw new RuntimeException("Bebida no encontrada");
                }
                break;
        }

        merma.calculateTotalCost();
        return mermaRepository.save(merma);
    }

    // Obtener todas las mermas
    public List<Merma> getAllMermas() {
        return mermaRepository.findAll();
    }

    // Obtener mermas por tipo
    public List<Merma> getMermasByType(MermaType type) {
        return mermaRepository.findByType(type);
    }

    // Obtener mermas por rango de fechas
    public List<Merma> getMermasByDateRange(LocalDateTime start, LocalDateTime end) {
        return mermaRepository.findByRegisteredAtBetween(start, end);
    }

    // Obtener mermas recientes
    public List<Merma> getRecentMermas() {
        return mermaRepository.findTop10ByOrderByRegisteredAtDesc();
    }

    // Obtener mermas por usuario
    public List<Merma> getMermasByUser(String userEmail) {
        return mermaRepository.findByRegisteredBy(userEmail);
    }

    // Obtener costo total de mermas por rango de fechas
    public BigDecimal getTotalCostByDateRange(LocalDateTime start, LocalDateTime end) {
        return mermaRepository.getTotalCostByDateRange(start, end);
    }

    // Obtener estadísticas de mermas por tipo
    public List<MermaStatsResponse> getMermaStatsByType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = mermaRepository.getMermaStatsByType(start, end);
        return results.stream()
                .map(result -> new MermaStatsResponse(
                        (MermaType) result[0],
                        (Long) result[1],
                        (BigDecimal) result[2]
                ))
                .collect(Collectors.toList());
    }

    // Obtener items con más desperdicio
    public List<ItemWasteStatsResponse> getItemsWithMostWaste(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = mermaRepository.getItemsWithMostWaste(start, end);
        return results.stream()
                .map(result -> new ItemWasteStatsResponse(
                        (String) result[0],
                        (MermaType) result[1],
                        (Long) result[2],
                        (BigDecimal) result[3]
                ))
                .collect(Collectors.toList());
    }

    // Obtener merma por ID
    public Optional<Merma> getMermaById(Long id) {
        return mermaRepository.findById(id);
    }

    // Eliminar merma (solo admin)
    public void deleteMerma(Long id) {
        mermaRepository.deleteById(id);
    }

    // Obtener mermas de hoy
    public List<Merma> getTodayMermas() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return mermaRepository.findByRegisteredAtBetween(startOfDay, endOfDay);
    }

    // Obtener costo total de mermas de hoy
    public BigDecimal getTodayTotalCost() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return mermaRepository.getTotalCostByDateRange(startOfDay, endOfDay);
    }
}
