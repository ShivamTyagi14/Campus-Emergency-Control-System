package com.campus.emergency.controller;

import com.campus.emergency.model.*;
import com.campus.emergency.repository.*;
import com.campus.emergency.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {
 private final AuthService auth; private final EmergencyService service;
 private final UserRepository users; private final TeamRepository teams; private final LocationRepository locations;
 private final InstructionRepository instructions;
 public ApiController(AuthService auth,EmergencyService service,UserRepository users,TeamRepository teams,LocationRepository locations,InstructionRepository instructions){
  this.auth=auth;this.service=service;this.users=users;this.teams=teams;this.locations=locations;this.instructions=instructions;
 }
 private AppUser current(HttpSession s){Object id=s.getAttribute("userId");if(id==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please log in");return users.findById((Long)id).orElseThrow();}
 private void requireRole(HttpSession s,Role role){if(current(s).getRole()!=role)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not authorized");}

 record RegisterRequest(@NotBlank String name,@Email String email,@Size(min=6) String password){}
 record LoginRequest(String email,String password){}
 record EmergencyRequest(String type,String location,String severity,String description,Integer peopleAffected){}
 record AssignRequest(Long teamId){}
 record StatusRequest(String status){}

 @PostMapping("/auth/register") public Map<String,Object> register(@RequestBody RegisterRequest r){
  return auth.safeUser(auth.register(r.name(),r.email(),r.password()));
 }
 @PostMapping("/auth/login") public Map<String,Object> login(@RequestBody LoginRequest r,HttpSession s){
  AppUser u=auth.login(r.email(),r.password());s.setAttribute("userId",u.getId());return auth.safeUser(u);
 }
 @PostMapping("/auth/logout") public Map<String,String> logout(HttpSession s){s.invalidate();return Map.of("message","Logged out");}
 @GetMapping("/auth/me") public Map<String,Object> me(HttpSession s){return auth.safeUser(current(s));}

 @PostMapping("/emergencies") public Emergency report(@RequestBody EmergencyRequest r,HttpSession s){
  AppUser u=current(s);
  return service.create(new Emergency(u.getName(),u.getEmail(),r.type(),r.location(),r.severity(),r.description(),r.peopleAffected()==null?1:r.peopleAffected()));
 }
 @GetMapping("/emergencies") public List<Emergency> emergencies(HttpSession s){
  AppUser u=current(s);
  if(u.getRole()==Role.ADMIN) return service.all();
  if(u.getRole()==Role.TEAM) return service.all().stream().filter(e->Objects.equals(e.getAssignedTeamId(),u.getTeamId())).toList();
  return service.byReporter(u.getEmail());
 }
 @GetMapping("/emergencies/prioritized") public List<Emergency> prioritized(HttpSession s){requireRole(s,Role.ADMIN);return service.getPrioritizedEmergencies();}
 @GetMapping("/emergencies/{id}") public Emergency emergency(@PathVariable Long id,HttpSession s){
  AppUser u=current(s);Emergency e=service.find(id);
  if(u.getRole()==Role.USER&&!e.getReporterEmail().equals(u.getEmail()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  return e;
 }
 @PostMapping("/emergencies/{id}/assign") public Emergency assign(@PathVariable Long id,@RequestBody AssignRequest r,HttpSession s){requireRole(s,Role.ADMIN);return service.assign(id,r.teamId());}
 @PostMapping("/emergencies/{id}/status") public Emergency status(@PathVariable Long id,@RequestBody StatusRequest r,HttpSession s){
  AppUser u=current(s);Emergency e=service.find(id);
  if(u.getRole()==Role.USER)throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  if(u.getRole()==Role.TEAM && !Objects.equals(e.getAssignedTeamId(), u.getTeamId()))
   throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not assigned to your team");
  try{return service.updateStatus(id,EmergencyStatus.valueOf(r.status()),u.getEmail());}
  catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status");}
 }
 @GetMapping("/teams") public List<EmergencyTeam> teamList(HttpSession s){requireRole(s,Role.ADMIN);return teams.findAll();}
 @GetMapping("/teams/available") public List<EmergencyTeam> availableTeams(HttpSession s){current(s);return service.availableTeams();}
 @GetMapping("/locations") public List<CampusLocation> locationList(HttpSession s){current(s);return locations.findAll();}
 @GetMapping("/instructions/{type}") public Map<String,String> advice(@PathVariable String type,HttpSession s){
  current(s);Emergency sample=new Emergency("","",""+type,"","LOW","",1);
  return Map.of("instruction",service.handlerFor(sample).safetyAdvice(sample));
 }
 @GetMapping("/emergencies/{id}/history") public List<EmergencyHistory> history(@PathVariable Long id,HttpSession s){current(s);return service.history(id);}
 @GetMapping("/admin/stats") public Map<String,Object> stats(HttpSession s){requireRole(s,Role.ADMIN);return service.stats();}
}