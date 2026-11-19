package com.campus.emergency.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="emergencies")
public class Emergency {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String reporterName;
    @Column(nullable=false) private String reporterEmail;
    @Column(nullable=false) private String type;
    @Column(nullable=false) private String location;
    @Column(nullable=false) private String severity;
    @Column(length=1500) private String description;
    @Enumerated(EnumType.STRING) private PriorityLevel priority;
    @Enumerated(EnumType.STRING) private EmergencyStatus status=EmergencyStatus.REPORTED;
    private Integer peopleAffected=1;
    private Integer etaMinutes;
    private Long assignedTeamId;
    private String assignedTeamName;
    private LocalDateTime reportedAt=LocalDateTime.now();
    private LocalDateTime resolvedAt;

    public Emergency() {}
    public Emergency(String reporterName,String reporterEmail,String type,String location,String severity,String description,Integer peopleAffected){
        this.reporterName=reporterName;this.reporterEmail=reporterEmail;this.type=type;this.location=location;
        this.severity=severity;this.description=description;this.peopleAffected=peopleAffected;
    }
    public Long getId(){return id;} public String getReporterName(){return reporterName;}
    public String getReporterEmail(){return reporterEmail;} public String getType(){return type;}
    public String getLocation(){return location;} public String getSeverity(){return severity;}
    public String getDescription(){return description;} public PriorityLevel getPriority(){return priority;}
    public void setPriority(PriorityLevel v){priority=v;} public EmergencyStatus getStatus(){return status;}
    public void setStatus(EmergencyStatus v){status=v;} public Integer getPeopleAffected(){return peopleAffected;}
    public Integer getEtaMinutes(){return etaMinutes;} public void setEtaMinutes(Integer v){etaMinutes=v;}
    public Long getAssignedTeamId(){return assignedTeamId;} public void setAssignedTeamId(Long v){assignedTeamId=v;}
    public String getAssignedTeamName(){return assignedTeamName;} public void setAssignedTeamName(String v){assignedTeamName=v;}
    public LocalDateTime getReportedAt(){return reportedAt;} public LocalDateTime getResolvedAt(){return resolvedAt;}
    public void setResolvedAt(LocalDateTime v){resolvedAt=v;}
}
