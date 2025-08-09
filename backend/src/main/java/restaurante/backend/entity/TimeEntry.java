package restaurante.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_entries")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TimeEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonIgnoreProperties({"timeEntries", "password"})
    private Worker worker;
    
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "work_date", nullable = false)
    private String workDate; // Format: YYYY-MM-DD
    
    @Column(name = "total_hours")
    private Double totalHours;
    
    @Column(name = "notes")
    private String notes;
    
    // Constructors
    public TimeEntry() {}
    
    public TimeEntry(Worker worker, LocalDateTime checkInTime, String workDate) {
        this.worker = worker;
        this.checkInTime = checkInTime;
        this.workDate = workDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Worker getWorker() {
        return worker;
    }
    
    public void setWorker(Worker worker) {
        this.worker = worker;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
        
        // Calculate total hours when check out time is set
        if (this.checkInTime != null && checkOutTime != null) {
            long minutes = java.time.Duration.between(this.checkInTime, checkOutTime).toMinutes();
            this.totalHours = minutes / 60.0;
        }
    }
    
    public String getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }
    
    public Double getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isCheckedOut() {
        return checkOutTime != null;
    }
    
    public String getFormattedCheckInTime() {
        return checkInTime != null ? checkInTime.toString() : null;
    }
    
    public String getFormattedCheckOutTime() {
        return checkOutTime != null ? checkOutTime.toString() : null;
    }
}
