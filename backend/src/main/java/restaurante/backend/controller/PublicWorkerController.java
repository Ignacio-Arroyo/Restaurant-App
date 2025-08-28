package restaurante.backend.controller;

import restaurante.backend.entity.Worker;
import restaurante.backend.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/public/workers")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicWorkerController {
    
    @Autowired
    private WorkerService workerService;
    
    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveWorkers() {
        try {
            List<Worker> workers = workerService.getActiveWorkers();
            
            // Solo devolver información básica para el login
            List<Map<String, Object>> workersInfo = workers.stream()
                .map(worker -> {
                    Map<String, Object> workerInfo = new HashMap<>();
                    workerInfo.put("id", worker.getId());
                    workerInfo.put("numeroEmpleado", worker.getNumeroEmpleado());
                    workerInfo.put("nombreCompleto", worker.getNombreCompleto());
                    workerInfo.put("email", worker.getEmail());
                    workerInfo.put("rol", worker.getRol().name());
                    return workerInfo;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(workersInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
