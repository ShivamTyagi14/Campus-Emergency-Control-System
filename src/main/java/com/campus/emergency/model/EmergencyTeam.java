    package com.campus.emergency.model;

    import jakarta.persistence.*;

    @Entity
    @Table(name="emergency_teams")
    public class EmergencyTeam {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
        @Column(nullable=false) private String teamName;
        @Column(nullable=false) private String specialty;
        private boolean available=true;
        private Integer estimatedMinutes=8;
        public EmergencyTeam() {}
        public EmergencyTeam(String teamName,String specialty,boolean available,Integer estimatedMinutes){
            this.teamName=teamName;this.specialty=specialty;this.available=available;this.estimatedMinutes=estimatedMinutes;
        }
        public Long getId(){return id;} public String getTeamName(){return teamName;}
        public void setTeamName(String v){teamName=v;} public String getSpecialty(){return specialty;}
        public void setSpecialty(String v){specialty=v;} public boolean isAvailable(){return available;}
        public void setAvailable(boolean v){available=v;} public Integer getEstimatedMinutes(){return estimatedMinutes;}
        public void setEstimatedMinutes(Integer v){estimatedMinutes=v;}
    }
