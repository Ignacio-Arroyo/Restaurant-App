package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.PromotionDTO;
import restaurante.backend.service.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend"})
public class PublicPromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    // Obtener promociones activas (público)
    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getAllActivePromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener promoción activa por ID (público)
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getActivePromotionById(@PathVariable Long id) {
        try {
            return promotionService.getPromotionById(id)
                    .filter(promotion -> promotion.getAvailable())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener promociones activas por tipo (público)
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PromotionDTO>> getActivePromotionsByType(@PathVariable String type) {
        try {
            List<PromotionDTO> promotions = promotionService.getPromotionsByType(type);
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
