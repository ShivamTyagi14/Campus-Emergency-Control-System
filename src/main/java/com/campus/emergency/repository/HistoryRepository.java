package com.campus.emergency.repository;
    import com.campus.emergency.model.EmergencyHistory;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.*;
    public interface HistoryRepository extends JpaRepository<EmergencyHistory, Long> {
    List<EmergencyHistory> findByEmergencyIdOrderByChangedAtDesc(Long emergencyId);
}
