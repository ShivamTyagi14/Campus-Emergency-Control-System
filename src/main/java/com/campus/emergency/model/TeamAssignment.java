package com.campus.emergency.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="team_assignments")
public class TeamAssignment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long emergencyId;
    private Long teamId;
    private LocalDateTime assignedAt=LocalDateTime.now();
    private String assignmentStatus="ASSIGNED";
    public TeamAssignment() {}
    public TeamAssignment(Long emergencyId,Long teamId){this.emergencyId=emergencyId;this.teamId=teamId;}
    public Long getId(){return id;} public Long getEmergencyId(){return emergencyId;}
    public Long getTeamId(){return teamId;} public String getAssignmentStatus(){return assignmentStatus;}
    public void setAssignmentStatus(String v){assignmentStatus=v;}
}
