package com.campus.emergency.service;
import com.campus.emergency.model.Emergency;
public class GeneralEmergencyHandler extends AbstractEmergencyHandler {
    public GeneralEmergencyHandler(Emergency e){super(e);}
    public int baseEtaMinutes(){return 10;}
    public String safetyAdvice(Emergency e){return "Move to a safe area, avoid the hazard, and keep access routes clear for responders. Follow instructions from campus security. Call local emergency services (112 in India) if anyone is in immediate danger.";}
}
