package com.campus.emergency.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="emergency_history")
public class EmergencyHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long emergencyId;
    private String action;
    private String changedBy;
    private LocalDateTime changedAt=LocalDateTime.now();
    public EmergencyHistory() {}
    public EmergencyHistory(Long emergencyId,String action,String changedBy){
        this.emergencyId=emergencyId;this.action=action;this.changedBy=changedBy;
    }
    public Long getId(){return id;} public Long getEmergencyId(){return emergencyId;}
    public String getAction(){return action;} public String getChangedBy(){return changedBy;}
    public LocalDateTime getChangedAt(){return changedAt;}
}
