package com.campus.emergency.service;
import com.campus.emergency.model.Emergency;
public class FireEmergencyHandler extends AbstractEmergencyHandler {
    public FireEmergencyHandler(Emergency e){super(e);}
    public int baseEtaMinutes(){return 5;}
    public String safetyAdvice(Emergency e){return "Raise the alarm and evacuate using the nearest safe exit. Do not use lifts. Stay low if there is smoke. Do not attempt to fight a spreading fire. Call local emergency services (112 in India).";}
}
