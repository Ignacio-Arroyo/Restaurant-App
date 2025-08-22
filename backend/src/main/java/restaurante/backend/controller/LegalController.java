package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserConsent;
import restaurante.backend.entity.UserConsent.ConsentType;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.service.ConsentService;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LegalController {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint para obtener términos y condiciones
     */
    @GetMapping("/legal/terms")
    public ResponseEntity<Map<String, Object>> getTermsAndConditions() {
        return ResponseEntity.ok(Map.of(
            "title", "Términos y Condiciones",
            "lastUpdated", "2025-08-21",
            "version", "1.0",
            "content", getTermsContent()
        ));
    }

    /**
     * Endpoint para obtener política de privacidad
     */
    @GetMapping("/legal/privacy")
    public ResponseEntity<Map<String, Object>> getPrivacyPolicy() {
        return ResponseEntity.ok(Map.of(
            "title", "Política de Privacidad",
            "lastUpdated", "2025-08-21",
            "version", "1.0",
            "content", getPrivacyContent()
        ));
    }

    /**
     * Actualizar consentimiento del usuario
     */
    @PostMapping("/user/consent")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateConsent(@RequestBody Map<String, Object> request,
                                               Principal principal,
                                               HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String consentTypeStr = (String) request.get("consentType");
        Boolean granted = (Boolean) request.get("granted");

        ConsentType consentType = ConsentType.valueOf(consentTypeStr.toUpperCase());
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (granted) {
            consentService.recordConsent(user, consentType, true, ipAddress, userAgent);
        } else {
            consentService.revokeConsent(user, consentType, ipAddress);
        }

        return ResponseEntity.ok("Consentimiento actualizado correctamente");
    }

    /**
     * Obtener consentimientos del usuario
     */
    @GetMapping("/user/consents")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserConsent>> getUserConsents(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UserConsent> consents = consentService.getUserConsents(user);
        return ResponseEntity.ok(consents);
    }

    /**
     * Revocar consentimiento de marketing (unsubscribe)
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribeFromMarketing(@RequestParam String email,
                                                          HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String ipAddress = getClientIpAddress(httpRequest);
        consentService.revokeConsent(user, ConsentType.MARKETING_EMAILS, ipAddress);

        return ResponseEntity.ok("Te has dado de baja exitosamente de nuestros emails de marketing");
    }

    /**
     * Endpoint para el derecho al olvido (GDPR)
     */
    @DeleteMapping("/user/data")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserData(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar consentimientos
        consentService.deleteUserConsents(user);
        
        // Aquí podrías agregar más lógica para eliminar otros datos del usuario
        
        return ResponseEntity.ok("Tus datos han sido eliminados conforme a tu derecho al olvido");
    }

    // Métodos helper
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getTermsContent() {
        return """
        <div class="legal-content">
            <div class="intro-section">
                <h2 class="main-title">📋 Términos y Condiciones de Uso</h2>
                <p class="intro-text">
                    Bienvenido a nuestro restaurante. Al utilizar nuestros servicios, usted acepta cumplir con estos términos y condiciones.
                    Por favor, léalos cuidadosamente antes de hacer uso de nuestra plataforma.
                </p>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">🤝 1. Aceptación de los Términos</h3>
                <div class="content-box">
                    <p>Al acceder y utilizar nuestro servicio de restaurante, usted acepta estar legalmente vinculado a estos términos y condiciones.
                    Si no está de acuerdo con alguna parte de estos términos, no debe utilizar nuestro servicio.</p>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">🍽️ 2. Uso del Servicio</h3>
                <div class="content-box">
                    <p>Nuestro servicio está diseñado para brindarle la mejor experiencia gastronómica:</p>
                    <div class="feature-list">
                        <div class="feature-item">
                            <span class="icon">🛒</span>
                            <div>
                                <strong>Realizar pedidos</strong>
                                <p>Ordene sus platillos favoritos de manera fácil y rápida</p>
                            </div>
                        </div>
                        <div class="feature-item">
                            <span class="icon">📱</span>
                            <div>
                                <strong>Consultar menú</strong>
                                <p>Explore nuestra variedad de platillos, bebidas y ofertas especiales</p>
                            </div>
                        </div>
                        <div class="feature-item">
                            <span class="icon">👤</span>
                            <div>
                                <strong>Gestionar cuenta</strong>
                                <p>Administre sus datos, historial de pedidos y preferencias</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">🔐 3. Cuenta de Usuario</h3>
                <div class="content-box">
                    <p>Para disfrutar de todas las funcionalidades de nuestro servicio, necesita crear una cuenta con información precisa y actualizada.</p>
                    <div class="highlight-box">
                        <strong>Importante:</strong> Usted es responsable de mantener la confidencialidad de su cuenta y contraseña.
                    </div>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">💳 4. Pedidos y Pagos</h3>
                <div class="content-box">
                    <div class="info-grid">
                        <div class="info-item">
                            <h4>💰 Precios</h4>
                            <p>Los precios pueden cambiar sin previo aviso y están sujetos a disponibilidad del producto.</p>
                        </div>
                        <div class="info-item">
                            <h4>🕒 Disponibilidad</h4>
                            <p>Los pedidos están sujetos a disponibilidad de ingredientes y horarios de operación.</p>
                        </div>
                        <div class="info-item">
                            <h4>✅ Confirmación</h4>
                            <p>Todos los pedidos deben ser confirmados antes de su preparación.</p>
                        </div>
                        <div class="info-item">
                            <h4>🔄 Cancelaciones</h4>
                            <p>Las cancelaciones deben realizarse antes del inicio de preparación.</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">⚖️ 5. Limitación de Responsabilidad</h3>
                <div class="content-box">
                    <div class="warning-box">
                        <p>El restaurante no será responsable por daños indirectos, incidentales, especiales o consecuentes que puedan surgir del uso de nuestro servicio.</p>
                    </div>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">📝 6. Modificaciones</h3>
                <div class="content-box">
                    <p>Nos reservamos el derecho de modificar estos términos en cualquier momento. Las modificaciones serán efectivas inmediatamente después de su publicación.</p>
                </div>
            </div>
            
            <div class="terms-section">
                <h3 class="section-title">📞 7. Contacto</h3>
                <div class="content-box">
                    <div class="contact-info">
                        <p>Para cualquier consulta sobre estos términos:</p>
                        <div class="contact-methods">
                            <div class="contact-item">
                                <span class="icon">📧</span>
                                <a href="mailto:legal@restaurant.com">legal@restaurant.com</a>
                            </div>
                            <div class="contact-item">
                                <span class="icon">📱</span>
                                <span>+1 (555) 123-4567</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        """;
    }

    private String getPrivacyContent() {
        return """
        <div class="legal-content">
            <div class="intro-section">
                <h2 class="main-title">🔒 Política de Privacidad</h2>
                <p class="intro-text">
                    Su privacidad es muy importante para nosotros. Esta política explica cómo recopilamos, usamos y protegemos 
                    su información personal cuando utiliza nuestros servicios.
                </p>
                <div class="gdpr-badge">
                    <span class="badge">🇪🇺 GDPR Compliant</span>
                    <span class="badge">🛡️ SSL Secured</span>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">📊 1. Información que Recopilamos</h3>
                <div class="content-box">
                    <p>Recopilamos diferentes tipos de información para brindarle el mejor servicio:</p>
                    <div class="data-types">
                        <div class="data-category">
                            <h4>👤 Información Personal</h4>
                            <ul class="data-list">
                                <li>Nombre completo</li>
                                <li>Dirección de correo electrónico</li>
                                <li>Número de teléfono</li>
                                <li>Dirección de entrega</li>
                            </ul>
                        </div>
                        <div class="data-category">
                            <h4>🛒 Información de Pedidos</h4>
                            <ul class="data-list">
                                <li>Historial de pedidos</li>
                                <li>Preferencias alimentarias</li>
                                <li>Métodos de pago</li>
                                <li>Calificaciones y reseñas</li>
                            </ul>
                        </div>
                        <div class="data-category">
                            <h4>💻 Información Técnica</h4>
                            <ul class="data-list">
                                <li>Dirección IP</li>
                                <li>Tipo de navegador</li>
                                <li>Datos de cookies</li>
                                <li>Ubicación aproximada</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">🎯 2. Cómo Utilizamos su Información</h3>
                <div class="content-box">
                    <div class="usage-grid">
                        <div class="usage-item">
                            <span class="usage-icon">🍽️</span>
                            <div>
                                <h4>Procesar Pedidos</h4>
                                <p>Gestionar y entregar sus pedidos de manera eficiente</p>
                            </div>
                        </div>
                        <div class="usage-item">
                            <span class="usage-icon">📧</span>
                            <div>
                                <h4>Comunicación</h4>
                                <p>Enviar confirmaciones, actualizaciones y soporte al cliente</p>
                            </div>
                        </div>
                        <div class="usage-item">
                            <span class="usage-icon">🎁</span>
                            <div>
                                <h4>Promociones</h4>
                                <p>Enviar ofertas especiales (solo con su consentimiento)</p>
                            </div>
                        </div>
                        <div class="usage-item">
                            <span class="usage-icon">📈</span>
                            <div>
                                <h4>Mejoras</h4>
                                <p>Analizar y mejorar nuestros servicios continuamente</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">🤝 3. Compartir Información</h3>
                <div class="content-box">
                    <div class="sharing-policy">
                        <div class="no-sharing">
                            <h4>❌ NO Compartimos</h4>
                            <p>Nunca vendemos, comerciamos o transferimos su información personal a terceros con fines comerciales.</p>
                        </div>
                        <div class="limited-sharing">
                            <h4>✅ Compartimos Solo Cuando</h4>
                            <ul>
                                <li>Es necesario para procesar su pedido (ej: servicio de entrega)</li>
                                <li>Lo requiere la ley o autoridades competentes</li>
                                <li>Es necesario para proteger nuestros derechos legales</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">🛡️ 4. Seguridad de Datos</h3>
                <div class="content-box">
                    <div class="security-measures">
                        <div class="security-item">
                            <span class="security-icon">🔐</span>
                            <div>
                                <h4>Cifrado SSL/TLS</h4>
                                <p>Todos los datos se transmiten de forma segura</p>
                            </div>
                        </div>
                        <div class="security-item">
                            <span class="security-icon">🏢</span>
                            <div>
                                <h4>Servidores Seguros</h4>
                                <p>Infraestructura protegida con las mejores prácticas</p>
                            </div>
                        </div>
                        <div class="security-item">
                            <span class="security-icon">👥</span>
                            <div>
                                <h4>Acceso Limitado</h4>
                                <p>Solo personal autorizado puede acceder a sus datos</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">⚖️ 5. Sus Derechos (GDPR)</h3>
                <div class="content-box">
                    <div class="rights-grid">
                        <div class="right-item">
                            <span class="right-icon">👁️</span>
                            <div>
                                <h4>Derecho de Acceso</h4>
                                <p>Puede solicitar una copia de todos sus datos personales</p>
                            </div>
                        </div>
                        <div class="right-item">
                            <span class="right-icon">✏️</span>
                            <div>
                                <h4>Derecho de Rectificación</h4>
                                <p>Puede corregir datos incorrectos o incompletos</p>
                            </div>
                        </div>
                        <div class="right-item">
                            <span class="right-icon">🗑️</span>
                            <div>
                                <h4>Derecho al Olvido</h4>
                                <p>Puede solicitar la eliminación de sus datos</p>
                            </div>
                        </div>
                        <div class="right-item">
                            <span class="right-icon">📦</span>
                            <div>
                                <h4>Portabilidad de Datos</h4>
                                <p>Puede exportar sus datos en formato legible</p>
                            </div>
                        </div>
                        <div class="right-item">
                            <span class="right-icon">⛔</span>
                            <div>
                                <h4>Derecho de Oposición</h4>
                                <p>Puede oponerse al procesamiento de sus datos</p>
                            </div>
                        </div>
                        <div class="right-item">
                            <span class="right-icon">⏸️</span>
                            <div>
                                <h4>Limitación del Tratamiento</h4>
                                <p>Puede solicitar restringir el uso de sus datos</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">🍪 6. Cookies y Tecnologías Similares</h3>
                <div class="content-box">
                    <div class="cookies-info">
                        <div class="cookie-type">
                            <h4>🔧 Cookies Esenciales</h4>
                            <p>Necesarias para el funcionamiento básico del sitio (no se pueden desactivar)</p>
                        </div>
                        <div class="cookie-type">
                            <h4>📊 Cookies Analíticas</h4>
                            <p>Nos ayudan a entender cómo los usuarios interactúan con nuestro sitio</p>
                        </div>
                        <div class="cookie-type">
                            <h4>🎯 Cookies de Marketing</h4>
                            <p>Se usan para mostrar anuncios relevantes (requieren su consentimiento)</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">⏱️ 7. Retención de Datos</h3>
                <div class="content-box">
                    <div class="retention-timeline">
                        <div class="retention-item">
                            <span class="timeline-dot"></span>
                            <div>
                                <h4>Datos de Cuenta</h4>
                                <p>Conservados mientras su cuenta esté activa</p>
                            </div>
                        </div>
                        <div class="retention-item">
                            <span class="timeline-dot"></span>
                            <div>
                                <h4>Historial de Pedidos</h4>
                                <p>Conservados por 3 años para fines fiscales y de servicio</p>
                            </div>
                        </div>
                        <div class="retention-item">
                            <span class="timeline-dot"></span>
                            <div>
                                <h4>Datos de Marketing</h4>
                                <p>Eliminados inmediatamente cuando revoca el consentimiento</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">📞 8. Contacto para Privacidad</h3>
                <div class="content-box">
                    <div class="contact-privacy">
                        <div class="contact-method">
                            <span class="contact-icon">📧</span>
                            <div>
                                <h4>Email de Privacidad</h4>
                                <a href="mailto:privacy@restaurant.com">privacy@restaurant.com</a>
                            </div>
                        </div>
                        <div class="contact-method">
                            <span class="contact-icon">📱</span>
                            <div>
                                <h4>Teléfono de Privacidad</h4>
                                <span>+1 (555) 123-4567 ext. 789</span>
                            </div>
                        </div>
                        <div class="contact-method">
                            <span class="contact-icon">🏢</span>
                            <div>
                                <h4>Dirección Postal</h4>
                                <p>Departamento de Privacidad<br>123 Restaurant Street<br>Ciudad, Estado 12345</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="privacy-section">
                <h3 class="section-title">📝 9. Cambios a esta Política</h3>
                <div class="content-box">
                    <div class="changes-policy">
                        <p>Podemos actualizar esta política de privacidad ocasionalmente. Cuando lo hagamos:</p>
                        <ul>
                            <li>✉️ Le notificaremos por email sobre cambios importantes</li>
                            <li>📱 Mostraremos una notificación en la aplicación</li>
                            <li>📅 Actualizaremos la fecha de "última modificación"</li>
                            <li>📋 Mantendremos un historial de versiones disponible</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        """;
    }
}
