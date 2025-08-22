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
    
    @Query("SELECT DISTINCT uc.user FROM UserConsent uc WHERE uc.consentType = :consentType AND uc.granted = true")
    List<User> findUsersWithActiveConsent(@Param("consentType") ConsentType consentType);
    
    @Query("SELECT COUNT(uc) > 0 FROM UserConsent uc WHERE uc.user = :user AND uc.consentType = :consentType AND uc.granted = true")
    boolean hasActiveConsent(@Param("user") User user, @Param("consentType") ConsentType consentType);
    
    void deleteByUser(User user);
}
