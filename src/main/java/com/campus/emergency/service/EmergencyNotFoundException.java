package com.campus.emergency.service;
public class EmergencyNotFoundException extends RuntimeException {
    public EmergencyNotFoundException(Long id){super("Emergency not found: "+id);}
}
