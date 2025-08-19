package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.Payment;
import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    Optional<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByOrderUserId(Long userId);
}
