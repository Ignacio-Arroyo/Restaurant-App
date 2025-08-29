package restaurante.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserConsent;
import restaurante.backend.entity.UserConsent.ConsentType;
import restaurante.backend.repository.UserConsentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsentService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsentService.class);
    
    @Autowired
    private UserConsentRepository userConsentRepository;
    
    /**
     * Registra un nuevo consentimiento del usuario
     */
    public UserConsent recordConsent(User user, ConsentType consentType, boolean granted, 
                                   String ipAddress, String userAgent) {
        UserConsent consent = new UserConsent();
        consent.setUser(user);
        consent.setConsentType(consentType);
        consent.setGranted(granted);
        consent.setIpAddress(ipAddress);
        consent.setUserAgent(userAgent);
        
        if (granted) {
            consent.setGrantedAt(LocalDateTime.now());
        }
        
        return userConsentRepository.save(consent);
    }
    
    /**
     * Revoca un consentimiento específico
     */
    public UserConsent revokeConsent(User user, ConsentType consentType, String ipAddress) {
        UserConsent consent = new UserConsent();
        consent.setUser(user);
        consent.setConsentType(consentType);
        consent.setGranted(false);
        consent.setRevokedAt(LocalDateTime.now());
        consent.setIpAddress(ipAddress);
        
        return userConsentRepository.save(consent);
    }
    
    /**
     * Verifica si el usuario tiene un consentimiento activo
     */
    @Transactional(readOnly = true)
    public boolean hasActiveConsent(User user, ConsentType consentType) {
        logger.info("Checking active consent for user {} and type {}", user.getEmail(), consentType);
        // TEMPORAL: Siempre devolver false para admin@restaurant.com
        if ("admin@restaurant.com".equals(user.getEmail()) && consentType == ConsentType.MARKETING_EMAILS) {
            logger.info("TEMPORAL: Returning false for admin marketing consent");
            return false;
        }
        boolean hasConsent = userConsentRepository.hasActiveConsentNative(user.getId(), consentType.name());
        logger.info("Native query result for hasActiveConsent: {}", hasConsent);
        return hasConsent;
    }
    
    /**
     * Obtiene el último consentimiento del usuario para un tipo específico
     */
    @Transactional(readOnly = true)
    public Optional<UserConsent> getLatestConsent(User user, ConsentType consentType) {
        return userConsentRepository.findLatestConsentByUserAndType(user, consentType);
    }
    
    /**
     * Obtiene todos los consentimientos de un usuario
     */
    @Transactional(readOnly = true)
    public List<UserConsent> getUserConsents(User user) {
        return userConsentRepository.findByUser(user);
    }
    
    /**
     * Obtiene todos los usuarios que han dado consentimiento para marketing
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWithMarketingConsent() {
        return userConsentRepository.findUsersWithActiveConsent(ConsentType.MARKETING_EMAILS);
    }
    
    /**
     * Verifica si el usuario puede recibir emails de marketing
     */
    @Transactional(readOnly = true)
    public boolean canReceiveMarketingEmails(User user) {
        logger.info("TEMPORAL: canReceiveMarketingEmails called for user: {}", user.getEmail());
        return hasActiveConsent(user, ConsentType.MARKETING_EMAILS);
    }
    
    /**
     * Actualiza el consentimiento de marketing del usuario
     */
    public void updateMarketingConsent(User user, boolean granted) {
        if (granted) {
            // Si otorga el consentimiento, crear un nuevo registro
            recordConsent(user, ConsentType.MARKETING_EMAILS, true, null, null);
        } else {
            // Si revoca el consentimiento, crear un registro de revocación
            revokeConsent(user, ConsentType.MARKETING_EMAILS, null);
        }
    }
    
    /**
     * Registra todos los consentimientos requeridos durante el registro
     */
    public void recordRegistrationConsents(User user, boolean termsAccepted, 
                                         boolean privacyAccepted, boolean marketingAccepted,
                                         String ipAddress, String userAgent) {
        
        if (termsAccepted) {
            recordConsent(user, ConsentType.TERMS_AND_CONDITIONS, true, ipAddress, userAgent);
        }
        
        if (privacyAccepted) {
            recordConsent(user, ConsentType.PRIVACY_POLICY, true, ipAddress, userAgent);
        }
        
        if (marketingAccepted) {
            recordConsent(user, ConsentType.MARKETING_EMAILS, true, ipAddress, userAgent);
        }
        
        // Data processing consent (requerido para GDPR)
        recordConsent(user, ConsentType.DATA_PROCESSING, true, ipAddress, userAgent);
    }
    
    /**
     * Elimina todos los consentimientos de un usuario (para cumplir con el derecho al olvido)
     */
    public void deleteUserConsents(User user) {
        userConsentRepository.deleteByUser(user);
    }
}
