package restaurante.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.dto.ChangePasswordRequest;
import restaurante.backend.dto.UpdateProfileRequest;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserConsent;
import restaurante.backend.service.UserService;
import restaurante.backend.service.ConsentService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ConsentService consentService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Obtener consentimiento de marketing
            boolean marketingConsent = consentService.canReceiveMarketingEmails(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "marketingConsent", marketingConsent
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user profile: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener el perfil del usuario");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/debug-consent")
    public ResponseEntity<Map<String, Object>> debugConsent() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Obtener consentimiento usando diferentes métodos
            boolean canReceiveEmails = consentService.canReceiveMarketingEmails(user);
            boolean hasActiveConsent = consentService.hasActiveConsent(user, UserConsent.ConsentType.MARKETING_EMAILS);
            
            // Obtener el último consentimiento directamente
            Optional<UserConsent> latestConsent = consentService.getLatestConsent(user, UserConsent.ConsentType.MARKETING_EMAILS);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userEmail", user.getEmail());
            response.put("canReceiveEmails", canReceiveEmails);
            response.put("hasActiveConsent", hasActiveConsent);
            response.put("latestConsentPresent", latestConsent.isPresent());
            response.put("latestConsentGranted", latestConsent.isPresent() ? latestConsent.get().getGranted() : null);
            response.put("latestConsentId", latestConsent.isPresent() ? latestConsent.get().getId() : null);
            response.put("latestConsentCreatedAt", latestConsent.isPresent() ? latestConsent.get().getCreatedAt() : null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in debug consent: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(404).body(response);
            }

            // Actualizar información del usuario
            boolean updated = false;
            
            if (request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(request.getFirstName());
                updated = true;
            }
            
            if (request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
                user.setLastName(request.getLastName());
                updated = true;
            }
            
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // Verificar que el nuevo email no esté en uso
                if (userService.existsByEmail(request.getEmail())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "El email ya está en uso por otro usuario");
                    return ResponseEntity.badRequest().body(response);
                }
                user.setEmail(request.getEmail());
                updated = true;
            }

            if (updated) {
                userService.save(user);
            }

            // Actualizar consentimiento de marketing si se proporciona
            if (request.getMarketingConsent() != null) {
                consentService.updateMarketingConsent(user, request.getMarketingConsent());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Perfil actualizado exitosamente");
            response.put("user", Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "marketingConsent", consentService.canReceiveMarketingEmails(user)
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating user profile: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar el perfil: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(404).body(response);
            }

            // Verificar que las nuevas contraseñas coincidan
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Las contraseñas nuevas no coinciden");
                return ResponseEntity.badRequest().body(response);
            }

            // Cambiar la contraseña
            boolean success = userService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Contraseña cambiada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "La contraseña actual no es correcta");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar la contraseña: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
