package restaurante.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurante.backend.entity.User;
import restaurante.backend.entity.Worker;
import restaurante.backend.repository.UserRepository;
import restaurante.backend.repository.WorkerRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try to find a Worker
        Worker worker = workerRepository.findByEmail(email).orElse(null);
        if (worker != null && worker.isActivo()) {
            return UserPrincipal.create(worker);
        }
        
        // If not found as Worker, try to find as User
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }
}
