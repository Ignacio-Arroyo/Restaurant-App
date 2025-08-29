package restaurante.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurante.backend.entity.User;
import restaurante.backend.entity.UserConsent;
import restaurante.backend.entity.UserConsent.ConsentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    
    List<UserConsent> findByUser(User user);
    
    List<UserConsent> findByUserAndConsentType(User user, ConsentType consentType);
    
    Optional<UserConsent> findByUserAndConsentTypeAndGrantedTrue(User user, ConsentType consentType);
    
    @Query("SELECT uc FROM UserConsent uc WHERE uc.user = :user AND uc.consentType = :consentType ORDER BY uc.createdAt DESC LIMIT 1")
    Optional<UserConsent> findLatestConsentByUserAndType(@Param("user") User user, @Param("consentType") ConsentType consentType);
    
    @Query("SELECT DISTINCT u FROM UserConsent uc JOIN uc.user u WHERE uc.consentType = :consentType AND uc.id IN (SELECT MAX(uc2.id) FROM UserConsent uc2 WHERE uc2.user = uc.user AND uc2.consentType = :consentType GROUP BY uc2.user) AND uc.granted = true")
    List<User> findUsersWithActiveConsent(@Param("consentType") ConsentType consentType);
    
    @Query(value = "SELECT CASE WHEN COUNT(uc.*) > 0 THEN true ELSE false END FROM user_consents uc WHERE uc.user_id = :userId AND uc.consent_type = :consentType AND uc.id = (SELECT MAX(uc2.id) FROM user_consents uc2 WHERE uc2.user_id = :userId AND uc2.consent_type = :consentType) AND uc.granted = true", nativeQuery = true)
    boolean hasActiveConsentNative(@Param("userId") Long userId, @Param("consentType") String consentType);
    
    void deleteByUser(User user);
}
