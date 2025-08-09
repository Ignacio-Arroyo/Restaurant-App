package restaurante.backend.controller;

import restaurante.backend.dto.CheckInRequest;
import restaurante.backend.dto.CheckOutRequest;
import restaurante.backend.entity.TimeEntry;
import restaurante.backend.service.TimeEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/time-entries")
@CrossOrigin(origins = "http://localhost:3000")
public class TimeEntryController {
    
    @Autowired
    private TimeEntryService timeEntryService;
    
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequest request) {
        try {
            TimeEntry timeEntry = timeEntryService.checkIn(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Entrada registrada exitosamente",
                "timeEntry", timeEntry,
                "employeeName", timeEntry.getWorker().getNombreCompleto(),
                "checkInTime", timeEntry.getFormattedCheckInTime()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }
    
    @PostMapping("/check-out")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkOut(@RequestBody CheckOutRequest request) {
        try {
            TimeEntry timeEntry = timeEntryService.checkOut(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Salida registrada exitosamente",
                "timeEntry", timeEntry,
                "employeeName", timeEntry.getWorker().getNombreCompleto(),
                "checkOutTime", timeEntry.getFormattedCheckOutTime(),
                "totalHours", timeEntry.getTotalHours()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("success", false, "error", "Error interno del servidor"));
        }
    }
    
    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntry>> getTodayTimeEntries() {
        try {
            List<TimeEntry> timeEntries = timeEntryService.getTodayTimeEntries();
            return ResponseEntity.ok(timeEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntry>> getActiveTimeEntries() {
        try {
            List<TimeEntry> timeEntries = timeEntryService.getActiveTimeEntries();
            return ResponseEntity.ok(timeEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/worker/{workerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntry>> getWorkerTimeEntries(@PathVariable Long workerId) {
        try {
            List<TimeEntry> timeEntries = timeEntryService.getWorkerTimeEntries(workerId);
            return ResponseEntity.ok(timeEntries);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntry>> getTimeEntriesByDateRange(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        try {
            List<TimeEntry> timeEntries = timeEntryService.getTimeEntriesByDateRange(startDate, endDate);
            return ResponseEntity.ok(timeEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/worker/{workerId}/hours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getWorkerTotalHours(
            @PathVariable Long workerId,
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        try {
            Optional<Double> totalHours = timeEntryService.getWorkerTotalHours(workerId, startDate, endDate);
            return ResponseEntity.ok(Map.of(
                "workerId", workerId,
                "startDate", startDate,
                "endDate", endDate,
                "totalHours", totalHours.orElse(0.0)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @GetMapping("/worker/status/{numeroEmpleado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getWorkerStatus(@PathVariable String numeroEmpleado) {
        try {
            Optional<TimeEntry> activeEntry = timeEntryService.getWorkerActiveTimeEntry(numeroEmpleado);
            boolean canCheckIn = timeEntryService.canWorkerCheckIn(numeroEmpleado);
            boolean canCheckOut = timeEntryService.canWorkerCheckOut(numeroEmpleado);
            
            if (activeEntry.isPresent()) {
                TimeEntry entry = activeEntry.get();
                return ResponseEntity.ok(Map.of(
                    "isActive", true,
                    "employeeName", entry.getWorker().getNombreCompleto(),
                    "checkInTime", entry.getFormattedCheckInTime(),
                    "workDate", entry.getWorkDate(),
                    "canCheckIn", canCheckIn,
                    "canCheckOut", canCheckOut,
                    "message", "Empleado tiene entrada activa - puede marcar salida"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "isActive", false,
                    "canCheckIn", canCheckIn,
                    "canCheckOut", canCheckOut,
                    "message", canCheckIn ? "Empleado puede marcar entrada" : "Empleado no puede marcar entrada (ya marcó hoy o está inactivo)"
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of(
                                    "error", e.getMessage(),
                                    "canCheckIn", false,
                                    "canCheckOut", false
                                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                    "error", "Error interno del servidor",
                                    "canCheckIn", false,
                                    "canCheckOut", false
                                ));
        }
    }
    
    @GetMapping("/worker/can-check-in/{numeroEmpleado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> canWorkerCheckIn(@PathVariable String numeroEmpleado) {
        try {
            boolean canCheckIn = timeEntryService.canWorkerCheckIn(numeroEmpleado);
            return ResponseEntity.ok(Map.of(
                "canCheckIn", canCheckIn,
                "message", canCheckIn ? "Puede marcar entrada" : "No puede marcar entrada"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "canCheckIn", false,
                "message", "Error al verificar estado"
            ));
        }
    }
    
    @GetMapping("/worker/can-check-out/{numeroEmpleado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> canWorkerCheckOut(@PathVariable String numeroEmpleado) {
        try {
            boolean canCheckOut = timeEntryService.canWorkerCheckOut(numeroEmpleado);
            return ResponseEntity.ok(Map.of(
                "canCheckOut", canCheckOut,
                "message", canCheckOut ? "Puede marcar salida" : "No puede marcar salida - debe marcar entrada primero"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "canCheckOut", false,
                "message", "Error al verificar estado"
            ));
        }
    }
}
