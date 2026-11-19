package com.campus.emergency.model;

import jakarta.persistence.*;

@Entity
public class CampusLocation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String name;
    private String building;
    public CampusLocation() {}
    public CampusLocation(String name, String building){this.name=name;this.building=building;}
    public Long getId(){return id;} public String getName(){return name;}
    public void setName(String name){this.name=name;} public String getBuilding(){return building;}
    public void setBuilding(String building){this.building=building;}
}
