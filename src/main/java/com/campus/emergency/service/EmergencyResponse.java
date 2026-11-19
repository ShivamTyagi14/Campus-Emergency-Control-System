package com.campus.emergency.service;
import com.campus.emergency.model.Emergency;
public interface EmergencyResponse {
    String safetyAdvice(Emergency emergency);
    int baseEtaMinutes();
}
