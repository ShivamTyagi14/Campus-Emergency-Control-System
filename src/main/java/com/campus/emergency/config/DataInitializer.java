package com.campus.emergency.config;

import com.campus.emergency.model.*;
import com.campus.emergency.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {
 @Bean CommandLineRunner seed(UserRepository users,TeamRepository teams,InstructionRepository instructions,LocationRepository locations){
  return args -> {
   BCryptPasswordEncoder enc=new BCryptPasswordEncoder();
   if(users.findByEmail("admin@campus.com").isEmpty()) 
       users.save(new AppUser("System Admin","admin@campus.com",enc.encode("Admin@123"), com.campus.emergency.model.Role.ADMIN));
   
   EmergencyTeam med=null, fire=null, sec=null, elec=null;
   if(teams.count()==0){
    med = teams.save(new EmergencyTeam("Medical Response Unit","MEDICAL",true,6));
    fire = teams.save(new EmergencyTeam("Fire & Safety Unit","FIRE",true,5));
    sec = teams.save(new EmergencyTeam("Campus Security","SECURITY",true,8));
    elec = teams.save(new EmergencyTeam("Electrical Maintenance","ELECTRICAL",true,12));
   } else {
    med = teams.findBySpecialty("MEDICAL").stream().findFirst().orElse(teams.findAll().get(0));
    fire = teams.findBySpecialty("FIRE").stream().findFirst().orElse(teams.findAll().get(1));
    sec = teams.findBySpecialty("SECURITY").stream().findFirst().orElse(teams.findAll().get(2));
    elec = teams.findBySpecialty("ELECTRICAL").stream().findFirst().orElse(teams.findAll().get(3));
   }

   // Seed user accounts for all 4 response teams linked to their team IDs
   if(users.findByEmail("medical@campus.com").isEmpty()) 
       users.save(new AppUser("Medical Responder","medical@campus.com",enc.encode("Team@123"), com.campus.emergency.model.Role.TEAM, med.getId()));
   if(users.findByEmail("fire@campus.com").isEmpty()) 
       users.save(new AppUser("Fire Responder","fire@campus.com",enc.encode("Team@123"), com.campus.emergency.model.Role.TEAM, fire.getId()));
   if(users.findByEmail("security@campus.com").isEmpty()) 
       users.save(new AppUser("Security Responder","security@campus.com",enc.encode("Team@123"), com.campus.emergency.model.Role.TEAM, sec.getId()));
   if(users.findByEmail("electrical@campus.com").isEmpty()) 
       users.save(new AppUser("Electrical Responder","electrical@campus.com",enc.encode("Team@123"), com.campus.emergency.model.Role.TEAM, elec.getId()));

   if(instructions.count()==0){
    instructions.save(new EmergencyInstruction("Fire","Evacuate using the nearest safe exit. Do not use lifts. Stay low if there is smoke."));
    instructions.save(new EmergencyInstruction("Medical","Do not move an injured person unless there is immediate danger. Ask someone to guide responders."));
    instructions.save(new EmergencyInstruction("Security","Move to a secure location, avoid confrontation and follow campus security instructions."));
    instructions.save(new EmergencyInstruction("Electrical","Keep away from exposed wires or sparks. Do not touch electrical equipment."));
   }
   if(locations.count()==0){
    for(String place:new String[]{"Main Gate","Library","Science Block","Computer Lab","Auditorium","Hostel","Cafeteria","Sports Ground"})
     locations.save(new CampusLocation(place,"Campus"));
   }
  };
 }
}