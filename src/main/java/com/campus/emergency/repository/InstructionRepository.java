package com.campus.emergency.repository;
    import com.campus.emergency.model.EmergencyInstruction;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.*;
    public interface InstructionRepository extends JpaRepository<EmergencyInstruction, Long> {
    List<EmergencyInstruction> findByEmergencyTypeIgnoreCase(String emergencyType);
}
