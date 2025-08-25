package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.PromotionDTO;
import restaurante.backend.service.PromotionService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/promotions")
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend"})
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    // Obtener todas las promociones para admin
    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getAllPromotionsForAdmin();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener promoción por ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        try {
            return promotionService.getPromotionById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Crear nueva promoción
    @PostMapping
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO createdPromotion = promotionService.createPromotion(promotionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Actualizar promoción
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionDTO);
            return ResponseEntity.ok(updatedPromotion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Alternar disponibilidad de promoción
    @PatchMapping("/{id}/availability")
    public ResponseEntity<?> togglePromotionAvailability(@PathVariable Long id) {
        try {
            PromotionDTO updatedPromotion = promotionService.togglePromotionAvailability(id);
            return ResponseEntity.ok(updatedPromotion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Eliminar promoción
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Promoción eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Buscar promociones por nombre
    @GetMapping("/search")
    public ResponseEntity<List<PromotionDTO>> searchPromotions(@RequestParam String name) {
        try {
            List<PromotionDTO> promotions = promotionService.searchPromotions(name);
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener promociones por tipo
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getPromotionsByType(@PathVariable String type) {
        try {
            List<PromotionDTO> promotions = promotionService.getPromotionsByType(type);
            return ResponseEntity.ok(promotions);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
