package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Coupon;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    // Buscar cupón por código
    Optional<Coupon> findByCode(String code);
    
    // Buscar cupón por código que esté activo
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.active = true")
    Optional<Coupon> findByCodeAndActive(@Param("code") String code);
    
    // Buscar todos los cupones activos
    List<Coupon> findByActiveTrue();
    
    // Buscar todos los cupones inactivos
    List<Coupon> findByActiveFalse();
    
    // Verificar si existe un cupón con el código dado
    boolean existsByCode(String code);
    
    // Buscar cupones por nombre (parcial)
    @Query("SELECT c FROM Coupon c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Coupon> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Buscar cupones ordenados por fecha de creación descendente
    @Query("SELECT c FROM Coupon c ORDER BY c.createdAt DESC")
    List<Coupon> findAllOrderByCreatedAtDesc();
    
    // Contar cupones activos
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.active = true")
    long countActiveCoupons();
    
    // Contar cupones inactivos
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.active = false")
    long countInactiveCoupons();
}
