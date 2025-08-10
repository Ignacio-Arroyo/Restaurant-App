package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurante.backend.dto.CouponDTO;
import restaurante.backend.dto.CouponValidationRequest;
import restaurante.backend.dto.CouponValidationResponse;
import restaurante.backend.entity.Coupon;
import restaurante.backend.repository.CouponRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    // Obtener todos los cupones
    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(CouponDTO::new)
                .collect(Collectors.toList());
    }
    
    // Obtener cupones activos
    public List<CouponDTO> getActiveCoupons() {
        return couponRepository.findByActiveTrue()
                .stream()
                .map(CouponDTO::new)
                .collect(Collectors.toList());
    }
    
    // Obtener cupón por ID
    public Optional<CouponDTO> getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(CouponDTO::new);
    }
    
    // Obtener cupón por código
    public Optional<CouponDTO> getCouponByCode(String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .map(CouponDTO::new);
    }
    
    // Crear nuevo cupón
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        // Verificar que el código no exista
        if (couponRepository.existsByCode(couponDTO.getCode().toUpperCase())) {
            throw new RuntimeException("Ya existe un cupón con el código: " + couponDTO.getCode());
        }
        
        Coupon coupon = couponDTO.toEntity();
        coupon.setCode(coupon.getCode().toUpperCase()); // Asegurar mayúsculas
        
        Coupon savedCoupon = couponRepository.save(coupon);
        return new CouponDTO(savedCoupon);
    }
    
    // Actualizar cupón existente
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado con ID: " + id));
        
        // Verificar que el código no esté siendo usado por otro cupón
        String newCode = couponDTO.getCode().toUpperCase();
        if (!existingCoupon.getCode().equals(newCode) && couponRepository.existsByCode(newCode)) {
            throw new RuntimeException("Ya existe un cupón con el código: " + newCode);
        }
        
        // Actualizar campos
        existingCoupon.setName(couponDTO.getName());
        existingCoupon.setCode(newCode);
        existingCoupon.setActive(couponDTO.getActive());
        existingCoupon.setDiscountType(couponDTO.getDiscountType());
        existingCoupon.setDiscountValue(couponDTO.getDiscountValue());
        existingCoupon.setMinimumPurchase(couponDTO.getMinimumPurchase());
        
        Coupon updatedCoupon = couponRepository.save(existingCoupon);
        return new CouponDTO(updatedCoupon);
    }
    
    // Eliminar cupón
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Cupón no encontrado con ID: " + id);
        }
        couponRepository.deleteById(id);
    }
    
    // Activar/Desactivar cupón
    public CouponDTO toggleCouponStatus(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado con ID: " + id));
        
        coupon.setActive(!coupon.getActive());
        Coupon updatedCoupon = couponRepository.save(coupon);
        return new CouponDTO(updatedCoupon);
    }
    
    // Validar cupón para aplicar descuento
    public CouponValidationResponse validateCoupon(CouponValidationRequest request) {
        String code = request.getCouponCode().toUpperCase();
        BigDecimal totalAmount = request.getTotalAmount();
        
        // Buscar el cupón por código
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        
        if (couponOpt.isEmpty()) {
            return CouponValidationResponse.invalidCode();
        }
        
        Coupon coupon = couponOpt.get();
        
        // Verificar si está activo
        if (!coupon.getActive()) {
            return CouponValidationResponse.inactive();
        }
        
        // Verificar compra mínima
        if (coupon.getMinimumPurchase() != null && 
            totalAmount.compareTo(coupon.getMinimumPurchase()) < 0) {
            return CouponValidationResponse.minimumNotMet(coupon.getMinimumPurchase());
        }
        
        // Calcular descuento
        BigDecimal discountAmount = coupon.calculateDiscount(totalAmount);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        
        return CouponValidationResponse.success(discountAmount, finalAmount, new CouponDTO(coupon));
    }
    
    // Buscar cupones por nombre
    public List<CouponDTO> searchCouponsByName(String name) {
        return couponRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(CouponDTO::new)
                .collect(Collectors.toList());
    }
    
    // Obtener estadísticas de cupones
    public CouponStatistics getCouponStatistics() {
        long totalCoupons = couponRepository.count();
        long activeCoupons = couponRepository.countActiveCoupons();
        long inactiveCoupons = couponRepository.countInactiveCoupons();
        
        return new CouponStatistics(totalCoupons, activeCoupons, inactiveCoupons);
    }
    
    // Clase interna para estadísticas
    public static class CouponStatistics {
        private long total;
        private long active;
        private long inactive;
        
        public CouponStatistics(long total, long active, long inactive) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
        }
        
        // Getters
        public long getTotal() { return total; }
        public long getActive() { return active; }
        public long getInactive() { return inactive; }
    }
}
