package restaurante.backend.service;

import restaurante.backend.dto.CreateWorkerRequest;
import restaurante.backend.entity.Worker;
import restaurante.backend.entity.WorkerRole;
import restaurante.backend.entity.TimeEntry;
import restaurante.backend.repository.WorkerRepository;
import restaurante.backend.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class WorkerService {
    
    @Autowired
    private WorkerRepository workerRepository;
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    private final Random random = new Random();
    
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }
    
    public List<Worker> getActiveWorkers() {
        return workerRepository.findActiveWorkersOrderedByHireDate();
    }
    
    public List<Worker> getWorkersByRole(WorkerRole role) {
        return workerRepository.findByRol(role);
    }
    
    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }
    
    public Optional<Worker> getWorkerByEmail(String email) {
        return workerRepository.findByEmail(email);
    }
    
    public Optional<Worker> getWorkerByEmployeeNumber(String numeroEmpleado) {
        return workerRepository.findByNumeroEmpleado(numeroEmpleado);
    }
    
    public Worker createWorker(CreateWorkerRequest request) {
        // Verificar que el email no exista
        if (workerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un trabajador con este email");
        }
        
        // Generar número de empleado único
        String numeroEmpleado = generateUniqueEmployeeNumber();
        
        Worker worker = new Worker(
            request.getNombre(),
            request.getApellido(),
            request.getDireccion(),
            request.getNumeroTelefono(),
            request.getEmail(),
            request.getRol(),
            numeroEmpleado,
            request.getNacionalidad(),
            request.getPassword()
        );
        
        return workerRepository.save(worker);
    }
    
    public Worker updateWorker(Long id, CreateWorkerRequest request) {
        Worker worker = workerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        // Verificar que el email no exista en otro trabajador
        Optional<Worker> existingWorker = workerRepository.findByEmail(request.getEmail());
        if (existingWorker.isPresent() && !existingWorker.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe un trabajador con este email");
        }
        
        worker.setNombre(request.getNombre());
        worker.setApellido(request.getApellido());
        worker.setDireccion(request.getDireccion());
        worker.setNumeroTelefono(request.getNumeroTelefono());
        worker.setEmail(request.getEmail());
        worker.setRol(request.getRol());
        worker.setNacionalidad(request.getNacionalidad());
        worker.setPassword(request.getPassword());
        
        return workerRepository.save(worker);
    }
    
    public void deactivateWorker(Long id) {
        Worker worker = workerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        // Close any active time entry for the worker being deactivated
        Optional<TimeEntry> activeEntry = timeEntryRepository.findActiveTimeEntryByWorker(worker);
        if (activeEntry.isPresent()) {
            TimeEntry entry = activeEntry.get();
            entry.setCheckOutTime(LocalDateTime.now());
            entry.setNotes("Checkout automático - empleado desactivado");
            timeEntryRepository.save(entry);
        }
        
        worker.setActivo(false);
        workerRepository.save(worker);
    }
    
    public void activateWorker(Long id) {
        Worker worker = workerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        worker.setActivo(true);
        workerRepository.save(worker);
    }
    
    public void deleteWorker(Long id) {
        Worker worker = workerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        // Delete all time entries associated with this worker first
        timeEntryRepository.deleteByWorker(worker);
        
        // Now we can safely delete the worker
        workerRepository.deleteById(id);
    }
    
    private String generateUniqueEmployeeNumber() {
        String numeroEmpleado;
        int attempts = 0;
        int maxAttempts = 100;
        
        do {
            numeroEmpleado = String.format("%06d", random.nextInt(1000000));
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new RuntimeException("No se pudo generar un número de empleado único");
            }
        } while (workerRepository.existsByNumeroEmpleado(numeroEmpleado));
        
        return numeroEmpleado;
    }
    
    public WorkerRole[] getAllRoles() {
        return WorkerRole.values();
    }
}
