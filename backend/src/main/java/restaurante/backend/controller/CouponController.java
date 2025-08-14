package restaurante.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.CouponDTO;
import restaurante.backend.dto.CouponValidationRequest;
import restaurante.backend.dto.CouponValidationResponse;
import restaurante.backend.service.CouponService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    // Obtener todos los cupones (solo admin y gerente)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        try {
            List<CouponDTO> coupons = couponService.getAllCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener cupones activos (solo admin y gerente)
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<List<CouponDTO>> getActiveCoupons() {
        try {
            List<CouponDTO> activeCoupons = couponService.getActiveCoupons();
            return ResponseEntity.ok(activeCoupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener cupón por ID (solo admin y gerente)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable Long id) {
        try {
            Optional<CouponDTO> coupon = couponService.getCouponById(id);
            return coupon.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Crear nuevo cupón (solo admin y gerente)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponDTO couponDTO) {
        try {
            CouponDTO createdCoupon = couponService.createCoupon(couponDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    // Actualizar cupón (solo admin y gerente)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, 
                                        @Valid @RequestBody CouponDTO couponDTO) {
        try {
            CouponDTO updatedCoupon = couponService.updateCoupon(id, couponDTO);
            return ResponseEntity.ok(updatedCoupon);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    // Eliminar cupón (solo admin y gerente)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deleteCoupon(id);
            return ResponseEntity.ok(new SuccessResponse("Cupón eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    // Activar/Desactivar cupón (solo admin y gerente)
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> toggleCouponStatus(@PathVariable Long id) {
        try {
            CouponDTO updatedCoupon = couponService.toggleCouponStatus(id);
            String status = updatedCoupon.getActive() ? "activado" : "desactivado";
            return ResponseEntity.ok(new SuccessResponse("Cupón " + status + " exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    // Validar cupón (público - para el proceso de compra)
    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@Valid @RequestBody CouponValidationRequest request) {
        try {
            CouponValidationResponse response = couponService.validateCoupon(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(new ErrorResponse("Error al validar el cupón"));
        }
    }
    
    // Buscar cupones por nombre (solo admin y gerente)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<List<CouponDTO>> searchCoupons(@RequestParam String name) {
        try {
            List<CouponDTO> coupons = couponService.searchCouponsByName(name);
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Obtener estadísticas de cupones (solo admin y gerente)
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<CouponService.CouponStatistics> getCouponStatistics() {
        try {
            CouponService.CouponStatistics stats = couponService.getCouponStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Clases auxiliares para respuestas
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
