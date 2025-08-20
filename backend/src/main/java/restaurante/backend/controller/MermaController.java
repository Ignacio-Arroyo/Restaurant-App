package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.CreateMermaRequest;
import restaurante.backend.dto.ItemWasteStatsResponse;
import restaurante.backend.dto.MermaStatsResponse;
import restaurante.backend.entity.Merma;
import restaurante.backend.entity.MermaType;
import restaurante.backend.service.MermaService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/merma")
@CrossOrigin(origins = "*")
public class MermaController {

    @Autowired
    private MermaService mermaService;

    // Crear nueva merma - Acceso para ADMIN, MANAGER, CHEF
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<?> createMerma(@Valid @RequestBody CreateMermaRequest request, 
                                         Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Merma merma = mermaService.createMerma(request, userEmail);
            return ResponseEntity.ok(merma);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Obtener todas las mermas - Solo ADMIN y MANAGER
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Merma>> getAllMermas() {
        List<Merma> mermas = mermaService.getAllMermas();
        return ResponseEntity.ok(mermas);
    }

    // Obtener mermas por tipo - ADMIN, MANAGER, CHEF
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<List<Merma>> getMermasByType(@PathVariable MermaType type) {
        List<Merma> mermas = mermaService.getMermasByType(type);
        return ResponseEntity.ok(mermas);
    }

    // Obtener mermas recientes - ADMIN, MANAGER, CHEF
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<List<Merma>> getRecentMermas() {
        List<Merma> mermas = mermaService.getRecentMermas();
        return ResponseEntity.ok(mermas);
    }

    // Obtener mermas de hoy - ADMIN, MANAGER, CHEF
    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<List<Merma>> getTodayMermas() {
        List<Merma> mermas = mermaService.getTodayMermas();
        return ResponseEntity.ok(mermas);
    }

    // Obtener costo total de mermas de hoy - ADMIN, MANAGER
    @GetMapping("/today/cost")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<String, BigDecimal>> getTodayTotalCost() {
        BigDecimal totalCost = mermaService.getTodayTotalCost();
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("totalCost", totalCost);
        return ResponseEntity.ok(response);
    }

    // Obtener mermas por rango de fechas - ADMIN, MANAGER
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Merma>> getMermasByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            
            List<Merma> mermas = mermaService.getMermasByDateRange(start, end);
            return ResponseEntity.ok(mermas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener costo total por rango de fechas - ADMIN, MANAGER
    @GetMapping("/date-range/cost")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<String, BigDecimal>> getTotalCostByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            
            BigDecimal totalCost = mermaService.getTotalCostByDateRange(start, end);
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("totalCost", totalCost);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener estadísticas por tipo - ADMIN, MANAGER
    @GetMapping("/stats/type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<MermaStatsResponse>> getMermaStatsByType(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            
            List<MermaStatsResponse> stats = mermaService.getMermaStatsByType(start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener items con más desperdicio - ADMIN, MANAGER
    @GetMapping("/stats/items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ItemWasteStatsResponse>> getItemsWithMostWaste(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            
            List<ItemWasteStatsResponse> stats = mermaService.getItemsWithMostWaste(start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener mermas de un usuario específico - ADMIN, MANAGER
    @GetMapping("/user/{userEmail}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Merma>> getMermasByUser(@PathVariable String userEmail) {
        List<Merma> mermas = mermaService.getMermasByUser(userEmail);
        return ResponseEntity.ok(mermas);
    }

    // Obtener merma por ID - ADMIN, MANAGER, CHEF
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<Merma> getMermaById(@PathVariable Long id) {
        return mermaService.getMermaById(id)
                .map(merma -> ResponseEntity.ok(merma))
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar merma - Solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMerma(@PathVariable Long id) {
        try {
            mermaService.deleteMerma(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Merma eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar la merma");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Obtener tipos de merma disponibles
    @GetMapping("/types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF')")
    public ResponseEntity<MermaType[]> getMermaTypes() {
        return ResponseEntity.ok(MermaType.values());
    }
}
