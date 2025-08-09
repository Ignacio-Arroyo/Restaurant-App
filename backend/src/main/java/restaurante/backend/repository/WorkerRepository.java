package restaurante.backend.repository;

import restaurante.backend.entity.Worker;
import restaurante.backend.entity.WorkerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    
    Optional<Worker> findByEmail(String email);
    
    Optional<Worker> findByNumeroEmpleado(String numeroEmpleado);
    
    List<Worker> findByRol(WorkerRole rol);
    
    List<Worker> findByActivoTrue();
    
    List<Worker> findByActivoFalse();
    
    @Query("SELECT w FROM Worker w WHERE w.activo = true ORDER BY w.fechaContratacion DESC")
    List<Worker> findActiveWorkersOrderedByHireDate();
    
    boolean existsByEmail(String email);
    
    boolean existsByNumeroEmpleado(String numeroEmpleado);
}
