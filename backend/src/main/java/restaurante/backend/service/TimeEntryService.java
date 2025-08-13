package restaurante.backend.service;

import restaurante.backend.dto.CheckInRequest;
import restaurante.backend.dto.CheckOutRequest;
import restaurante.backend.entity.TimeEntry;
import restaurante.backend.entity.Worker;
import restaurante.backend.repository.TimeEntryRepository;
import restaurante.backend.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TimeEntryService {
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private WorkerRepository workerRepository;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public TimeEntry checkIn(CheckInRequest request) {
        // Validate worker credentials
        Worker worker = validateWorkerCredentials(request.getNumeroEmpleado(), request.getPassword());
        
        // Check if worker is active
        if (!worker.isActivo()) {
            throw new IllegalArgumentException("El empleado está inactivo y no puede marcar entrada");
        }
        
        // Get today's date
        String today = LocalDateTime.now().format(dateFormatter);
        
        // Check if worker already has an active time entry (not checked out)
        Optional<TimeEntry> activeEntry = timeEntryRepository.findActiveTimeEntryByWorker(worker);
        if (activeEntry.isPresent()) {
            throw new IllegalArgumentException("El empleado ya tiene una entrada activa sin marcar salida. Debe marcar salida antes de poder marcar entrada nuevamente.");
        }
        
        // Create new time entry - Allow multiple check-ins per day as long as previous entries have check-out
        TimeEntry timeEntry = new TimeEntry(worker, LocalDateTime.now(), today);
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry checkOut(CheckOutRequest request) {
        // Validate worker credentials
        Worker worker = validateWorkerCredentials(request.getNumeroEmpleado(), request.getPassword());
        
        // Find active time entry for this worker (entrada sin salida)
        Optional<TimeEntry> activeEntry = timeEntryRepository.findActiveTimeEntryByWorker(worker);
        if (activeEntry.isEmpty()) {
            throw new IllegalArgumentException("No puede marcar salida sin haber marcado entrada primero. No hay una entrada activa para este empleado.");
        }
        
        TimeEntry timeEntry = activeEntry.get();
        
        // Validate that the check-in was today (additional security)
        String today = LocalDateTime.now().format(dateFormatter);
        if (!timeEntry.getWorkDate().equals(today)) {
            throw new IllegalArgumentException("No puede marcar salida para una entrada de otro día. Consulte con el administrador.");
        }
        
        // Set check out time
        timeEntry.setCheckOutTime(LocalDateTime.now());
        
        // Set notes if provided
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            timeEntry.setNotes(request.getNotes().trim());
        }
        
        return timeEntryRepository.save(timeEntry);
    }
    
    public List<TimeEntry> getTodayTimeEntries() {
        String today = LocalDateTime.now().format(dateFormatter);
        return timeEntryRepository.findByWorkDate(today);
    }
    
    public List<TimeEntry> getActiveTimeEntries() {
        return timeEntryRepository.findActiveTimeEntries();
    }
    
    public List<TimeEntry> getWorkerTimeEntries(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        return timeEntryRepository.findByWorkerOrderByWorkDateDesc(worker);
    }
    
    public List<TimeEntry> getTimeEntriesByDateRange(String startDate, String endDate) {
        return timeEntryRepository.findByWorkDateBetween(startDate, endDate);
    }
    
    public Optional<Double> getWorkerTotalHours(Long workerId, String startDate, String endDate) {
        Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        return timeEntryRepository.calculateTotalHoursByWorkerAndDateRange(worker, startDate, endDate);
    }
    
    public Optional<TimeEntry> getWorkerActiveTimeEntry(String numeroEmpleado) {
        Worker worker = workerRepository.findByNumeroEmpleado(numeroEmpleado)
            .orElseThrow(() -> new IllegalArgumentException("Número de empleado no encontrado"));
        return timeEntryRepository.findActiveTimeEntryByWorker(worker);
    }
    
    public boolean canWorkerCheckOut(String numeroEmpleado) {
        try {
            Worker worker = workerRepository.findByNumeroEmpleado(numeroEmpleado)
                .orElse(null);
            if (worker == null || !worker.isActivo()) {
                return false;
            }
            
            // Check if worker has an active entry (check-in without check-out)
            Optional<TimeEntry> activeEntry = timeEntryRepository.findActiveTimeEntryByWorker(worker);
            if (activeEntry.isEmpty()) {
                return false;
            }
            
            // Verify that the active entry is from today
            String today = LocalDateTime.now().format(dateFormatter);
            return activeEntry.get().getWorkDate().equals(today);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean canWorkerCheckIn(String numeroEmpleado) {
        try {
            Worker worker = workerRepository.findByNumeroEmpleado(numeroEmpleado)
                .orElse(null);
            if (worker == null || !worker.isActivo()) {
                return false;
            }
            
            // Check if worker already has an active entry (check-in without check-out)
            Optional<TimeEntry> activeEntry = timeEntryRepository.findActiveTimeEntryByWorker(worker);
            if (activeEntry.isPresent()) {
                return false; // Can't check in if already checked in and hasn't checked out
            }
            
            // Allow check-in if no active entry exists (regardless of previous entries today)
            // This allows multiple check-ins per day as long as they have checked out between them
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Worker validateWorkerCredentials(String numeroEmpleado, String password) {
        Worker worker = workerRepository.findByNumeroEmpleado(numeroEmpleado)
            .orElseThrow(() -> new IllegalArgumentException("Número de empleado no válido"));
        
        if (!worker.getPassword().equals(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        
        return worker;
    }
}
