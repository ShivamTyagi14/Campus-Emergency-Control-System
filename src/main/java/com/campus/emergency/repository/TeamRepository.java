package com.campus.emergency.repository;

import com.campus.emergency.model.EmergencyTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamRepository extends JpaRepository<EmergencyTeam, Long> {
    List<EmergencyTeam> findByAvailableTrue();
    List<EmergencyTeam> findBySpecialty(String specialty);
}