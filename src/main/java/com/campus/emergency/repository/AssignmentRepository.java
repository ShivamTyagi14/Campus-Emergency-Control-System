package com.campus.emergency.repository;
    import com.campus.emergency.model.TeamAssignment;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.*;
    public interface AssignmentRepository extends JpaRepository<TeamAssignment, Long> {
    List<TeamAssignment> findByTeamId(Long teamId);
}
