package com.campus.emergency.service;
import com.campus.emergency.model.Emergency;
public class MedicalEmergencyHandler extends AbstractEmergencyHandler {
    public MedicalEmergencyHandler(Emergency e){super(e);}
    public int baseEtaMinutes(){return 6;}
    public String safetyAdvice(Emergency e){return "Stay calm. Do not move an injured person unless there is immediate danger. Ask a nearby staff member to guide responders. For life-threatening danger, call local emergency services (112 in India).";}
}
