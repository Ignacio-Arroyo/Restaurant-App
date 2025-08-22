package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.service.EmailService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/test-welcome")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> testWelcomeEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "Test") String firstName,
            @RequestParam(defaultValue = "User") String lastName) {
        try {
            emailService.sendWelcomeEmail(email, firstName, lastName);
            return ResponseEntity.ok().body("Email de prueba enviado a: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error enviando email: " + e.getMessage());
        }
    }

    @PostMapping("/test-simple-welcome")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> testSimpleWelcomeEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "Test") String firstName,
            @RequestParam(defaultValue = "User") String lastName) {
        try {
            emailService.sendSimpleWelcomeEmail(email, firstName, lastName);
            return ResponseEntity.ok().body("Email simple de prueba enviado a: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error enviando email: " + e.getMessage());
        }
    }
}
