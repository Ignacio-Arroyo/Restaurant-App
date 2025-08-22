package restaurante.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserRole;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.service.EmailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/marketing")
@PreAuthorize("hasRole('GERENTE') or hasRole('ADMIN')")
public class MarketingController {

    private static final Logger logger = LoggerFactory.getLogger(MarketingController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMarketingEmail(@RequestBody MarketingEmailRequest request) {
        logger.info("Received marketing email request from admin");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar que se proporcionen los datos requeridos
            if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El asunto del email es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El contenido del email es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Enviar a todos los clientes
            emailService.sendMarketingEmailToAllCustomers(request.getSubject(), request.getContent());
            
            // Obtener el número de clientes para la respuesta
            List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);
            long customerCount = customers.stream()
                .filter(user -> user.getEmail() != null && !user.getEmail().isEmpty())
                .count();
            
            response.put("success", true);
            response.put("message", "Email de marketing enviado exitosamente");
            response.put("customersCount", customerCount);
            
            logger.info("Marketing email sent to {} customers", customerCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending marketing email: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error al enviar el email de marketing: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/customers/count")
    public ResponseEntity<Map<String, Object>> getCustomerCount() {
        try {
            List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);
            long customerCount = customers.stream()
                .filter(user -> user.getEmail() != null && !user.getEmail().isEmpty())
                .count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customersCount", customerCount);
            response.put("totalCustomers", customers.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting customer count: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener el número de clientes");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Clase interna para el request
    public static class MarketingEmailRequest {
        private String subject;
        private String content;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
