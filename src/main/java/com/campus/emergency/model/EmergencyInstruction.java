package com.campus.emergency.model;

import jakarta.persistence.*;

@Entity
public class EmergencyInstruction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String emergencyType;
    @Column(length=2000) private String instruction;
    public EmergencyInstruction() {}
    public EmergencyInstruction(String emergencyType,String instruction){this.emergencyType=emergencyType;this.instruction=instruction;}
    public Long getId(){return id;} public String getEmergencyType(){return emergencyType;}
    public String getInstruction(){return instruction;}
}
