package restaurante.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import restaurante.backend.dto.AuthResponse;
import restaurante.backend.dto.LoginRequest;
import restaurante.backend.dto.RegisterRequest;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserRole;
import restaurante.backend.entity.Worker;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.repository.WorkerRepository;
import restaurante.backend.security.JwtUtils;
import restaurante.backend.security.UserPrincipal;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConsentService consentService;

    public AuthResponse login(LoginRequest loginRequest) {
        // Primero intentar autenticar como Worker
        Worker worker = workerRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        
        if (worker != null && worker.isActivo() && 
            passwordEncoder.matches(loginRequest.getPassword(), worker.getPassword())) {
            
            String jwt = jwtUtils.generateJwtToken(worker.getEmail(), worker.getRol().name());
            return new AuthResponse(jwt, worker.getEmail(), worker.getNombre(), 
                                   worker.getApellido(), worker.getRol().name(), worker.getNumeroEmpleado());
        }
        
        // Si no es un worker, intentar autenticar como User normal
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String jwt = jwtUtils.generateJwtToken(userPrincipal.getUsername(), user.getRole().name());

            return new AuthResponse(jwt, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name());
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        
        if (workerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken by a worker!");
        }

        User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
        );

        user.setRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        // Registrar consentimientos requeridos (todos true por defecto en el registro)
        try {
            consentService.recordRegistrationConsents(
                savedUser, 
                true, // términos y condiciones (requerido)
                true, // política de privacidad (requerido)
                registerRequest.getMarketingConsent() != null ? registerRequest.getMarketingConsent() : false, // marketing (opcional)
                registerRequest.getIpAddress(),
                registerRequest.getUserAgent()
            );
        } catch (Exception e) {
            System.err.println("Error recording consents: " + e.getMessage());
        }

        // Enviar email de bienvenida de forma asíncrona
        try {
            emailService.sendWelcomeEmail(
                savedUser.getEmail(), 
                savedUser.getFirstName(), 
                savedUser.getLastName()
            );
        } catch (Exception e) {
            // Log del error pero no afectar el registro
            System.err.println("Error sending welcome email: " + e.getMessage());
        }

        String jwt = jwtUtils.generateJwtToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponse(jwt, savedUser.getEmail(), savedUser.getFirstName(), 
                               savedUser.getLastName(), savedUser.getRole().name());
    }
}
