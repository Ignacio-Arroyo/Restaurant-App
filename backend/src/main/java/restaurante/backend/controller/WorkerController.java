package restaurante.backend.controller;

import restaurante.backend.dto.CreateWorkerRequest;
import restaurante.backend.entity.Worker;
import restaurante.backend.entity.WorkerRole;
import restaurante.backend.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/workers")
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkerController {
    
    @Autowired
    private WorkerService workerService;
    
    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        try {
            List<Worker> workers = workerService.getAllWorkers();
            return ResponseEntity.ok(workers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Worker>> getActiveWorkers() {
        try {
            List<Worker> workers = workerService.getActiveWorkers();
            return ResponseEntity.ok(workers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Worker>> getWorkersByRole(@PathVariable WorkerRole role) {
        try {
            List<Worker> workers = workerService.getWorkersByRole(role);
            return ResponseEntity.ok(workers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/roles")
    public ResponseEntity<WorkerRole[]> getAllRoles() {
        try {
            WorkerRole[] roles = workerService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long id) {
        try {
            Optional<Worker> worker = workerService.getWorkerById(id);
            return worker.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createWorker(@RequestBody CreateWorkerRequest request) {
        try {
            Worker worker = workerService.createWorker(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(worker);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorker(@PathVariable Long id, 
                                         @RequestBody CreateWorkerRequest request) {
        try {
            Worker worker = workerService.updateWorker(id, request);
            return ResponseEntity.ok(worker);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateWorker(@PathVariable Long id) {
        try {
            workerService.deactivateWorker(id);
            return ResponseEntity.ok(Map.of("message", "Trabajador desactivado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateWorker(@PathVariable Long id) {
        try {
            workerService.activateWorker(id);
            return ResponseEntity.ok(Map.of("message", "Trabajador activado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorker(@PathVariable Long id) {
        try {
            workerService.deleteWorker(id);
            return ResponseEntity.ok(Map.of("message", "Trabajador eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
