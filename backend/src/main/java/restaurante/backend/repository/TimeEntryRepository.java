package restaurante.backend.repository;

import restaurante.backend.entity.TimeEntry;
import restaurante.backend.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    // Find today's time entry for a worker
    Optional<TimeEntry> findByWorkerAndWorkDate(Worker worker, String workDate);
    
    // Find all time entries for a worker
    List<TimeEntry> findByWorkerOrderByWorkDateDesc(Worker worker);
    
    // Find time entries by date range
    @Query("SELECT t FROM TimeEntry t WHERE t.workDate BETWEEN :startDate AND :endDate ORDER BY t.workDate DESC, t.checkInTime DESC")
    List<TimeEntry> findByWorkDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // Find active (not checked out) time entries
    @Query("SELECT t FROM TimeEntry t WHERE t.checkOutTime IS NULL ORDER BY t.checkInTime DESC")
    List<TimeEntry> findActiveTimeEntries();
    
    // Find active time entry for a specific worker
    @Query("SELECT t FROM TimeEntry t WHERE t.worker = :worker AND t.checkOutTime IS NULL")
    Optional<TimeEntry> findActiveTimeEntryByWorker(@Param("worker") Worker worker);
    
    // Find time entries for a worker in a date range
    @Query("SELECT t FROM TimeEntry t WHERE t.worker = :worker AND t.workDate BETWEEN :startDate AND :endDate ORDER BY t.workDate DESC")
    List<TimeEntry> findByWorkerAndWorkDateBetween(@Param("worker") Worker worker, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // Calculate total hours worked by a worker in a date range
    @Query("SELECT SUM(t.totalHours) FROM TimeEntry t WHERE t.worker = :worker AND t.workDate BETWEEN :startDate AND :endDate AND t.totalHours IS NOT NULL")
    Optional<Double> calculateTotalHoursByWorkerAndDateRange(@Param("worker") Worker worker, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // Find today's time entries
    List<TimeEntry> findByWorkDate(String workDate);
    
    // Delete all time entries for a worker (used when deleting a worker)
    void deleteByWorker(Worker worker);
}
