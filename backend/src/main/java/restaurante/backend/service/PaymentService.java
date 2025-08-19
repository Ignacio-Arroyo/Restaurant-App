package restaurante.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.dto.PaymentIntentRequest;
import restaurante.backend.dto.PaymentIntentResponse;
import restaurante.backend.entity.Order;
import restaurante.backend.entity.Payment;
import restaurante.backend.repository.OrderRepository;
import restaurante.backend.repository.PaymentRepository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) {
        try {
            // Verificar que la orden existe
            Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getOrderId()));

            // Verificar que la orden no esté ya pagada
            if (Boolean.TRUE.equals(order.getPaid())) {
                return new PaymentIntentResponse("Order is already paid");
            }

            // Verificar si ya existe un payment para esta orden
            Optional<Payment> existingPayment = paymentRepository.findByOrderId(request.getOrderId());
            if (existingPayment.isPresent() && 
                existingPayment.get().getStatus() != Payment.PaymentStatus.FAILED &&
                existingPayment.get().getStatus() != Payment.PaymentStatus.CANCELLED) {
                
                Payment payment = existingPayment.get();
                return new PaymentIntentResponse(
                    payment.getId(),
                    payment.getStripeClientSecret(),
                    payment.getStripePaymentIntentId()
                );
            }

            // Convertir amount a centavos (Stripe maneja centavos)
            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            // Crear PaymentIntent en Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(request.getCurrency().toLowerCase())
                .addPaymentMethodType(request.getPaymentMethodTypes())
                .putMetadata("order_id", String.valueOf(request.getOrderId()))
                .putMetadata("restaurant", "Restaurant App")
                .setDescription("Payment for Order #" + request.getOrderId())
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Crear registro de Payment en la BD
            Payment payment = new Payment(order, request.getAmount(), request.getCurrency());
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setStripeClientSecret(paymentIntent.getClientSecret());
            payment.setStatus(Payment.PaymentStatus.PENDING);

            payment = paymentRepository.save(payment);

            return new PaymentIntentResponse(
                payment.getId(),
                paymentIntent.getClientSecret(),
                paymentIntent.getId()
            );

        } catch (StripeException e) {
            throw new RuntimeException("Error creating payment intent: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Payment confirmPayment(String stripePaymentIntentId) {
        try {
            // Obtener el PaymentIntent de Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);

            // Buscar el payment en la BD
            Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for PaymentIntent: " + stripePaymentIntentId));

            // Actualizar el estado según el estado de Stripe
            switch (paymentIntent.getStatus()) {
                case "succeeded":
                    payment.setStatus(Payment.PaymentStatus.COMPLETED);
                    // Marcar la orden como pagada
                    orderService.updateOrderPaidStatus(payment.getOrder().getId(), true);
                    break;
                case "processing":
                    payment.setStatus(Payment.PaymentStatus.PROCESSING);
                    break;
                case "canceled":
                    payment.setStatus(Payment.PaymentStatus.CANCELLED);
                    break;
                default:
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason("Payment failed with status: " + paymentIntent.getStatus());
                    break;
            }

            return paymentRepository.save(payment);

        } catch (StripeException e) {
            throw new RuntimeException("Error confirming payment: " + e.getMessage(), e);
        }
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElse(null);
    }

    public Payment getPaymentByStripeIntentId(String stripePaymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElse(null);
    }

    @Transactional
    public void handleStripeWebhook(Map<String, Object> event) {
        String eventType = (String) event.get("type");
        
        if ("payment_intent.succeeded".equals(eventType)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> paymentIntentData = (Map<String, Object>) ((Map<String, Object>) event.get("data")).get("object");
            String paymentIntentId = (String) paymentIntentData.get("id");
            
            confirmPayment(paymentIntentId);
        }
    }
}
