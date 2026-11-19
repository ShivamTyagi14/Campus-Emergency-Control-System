
package com.campus.emergency.repository;

import com.campus.emergency.model.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyRepository extends JpaRepository<Emergency, Long> {

    List<Emergency> findAllByOrderByReportedAtDesc();

    List<Emergency> findByReporterEmailOrderByReportedAtDesc(String email);
}
