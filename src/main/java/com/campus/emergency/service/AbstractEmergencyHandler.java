package com.campus.emergency.service;
import com.campus.emergency.model.Emergency;
public abstract class AbstractEmergencyHandler implements EmergencyResponse {
    protected final Emergency emergency;
    protected AbstractEmergencyHandler(Emergency emergency){this.emergency=emergency;}
    public abstract String safetyAdvice(Emergency emergency);
}
