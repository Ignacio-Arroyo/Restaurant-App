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
import restaurante.backend.repository.UserRepository;
import restaurante.backend.security.JwtUtils;
import restaurante.backend.security.UserPrincipal;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userPrincipal.getUsername());

        User user = userRepository.findByEmail(userPrincipal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(jwt, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
        );

        user.setRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        String jwt = jwtUtils.generateJwtToken(savedUser.getEmail());

        return new AuthResponse(jwt, savedUser.getEmail(), savedUser.getFirstName(), 
                               savedUser.getLastName(), savedUser.getRole().name());
    }
}
